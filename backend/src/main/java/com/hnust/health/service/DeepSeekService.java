package com.hnust.health.service;

import com.hnust.health.config.DeepSeekConfig;
import com.hnust.health.entity.PlanHistory;
import com.hnust.health.entity.UserProfile;
import com.hnust.health.entity.WeightRecord;
import com.hnust.health.mapper.PlanHistoryMapper;
import com.hnust.health.mapper.WeightRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final DeepSeekConfig config;
    private final WeightRecordMapper weightRecordMapper;
    private final PlanHistoryMapper planHistoryMapper;

    // 记录用户上次调用时间，防止频繁调用（5分钟间隔）
    private final Map<Long, LocalDateTime> lastCallTime = new ConcurrentHashMap<>();
    private static final int CALL_INTERVAL_MINUTES = 1;

    public String generatePlan(UserProfile profile, Long userId, 
                               String intensity, String duration, String diet) {
        // 检查调用频率
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastCall = lastCallTime.get(userId);
        if (lastCall != null && now.minusMinutes(CALL_INTERVAL_MINUTES).isBefore(lastCall)) {
            long secondsLeft = CALL_INTERVAL_MINUTES * 60 - java.time.Duration.between(lastCall, now).getSeconds();
            return "ERROR:FREQUENCY_LIMIT:请等待 " + (secondsLeft / 60 + 1) + " 分钟后再生成新计划";
        }

        // 1. 查询历史体重记录（最近3条，减少Prompt长度）
        List<WeightRecord> records = weightRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WeightRecord>()
                .eq(WeightRecord::getUserId, userId)
                .orderByDesc(WeightRecord::getRecordDate)
                .last("LIMIT 3")
        );

        // 2. 构建体重变化描述
        StringBuilder weightContext = new StringBuilder();
        if (records.isEmpty()) {
            weightContext.append("暂无历史体重记录。");
        } else {
            for (WeightRecord r : records) {
                weightContext.append(String.format("%s: %.1fkg; ", 
                    r.getRecordDate().format(DateTimeFormatter.ofPattern("MM-dd")), r.getWeight()));
            }
            if (records.size() >= 2) {
                double latest = records.get(0).getWeight();
                double earliest = records.get(records.size() - 1).getWeight();
                double change = latest - earliest;
                weightContext.append(String.format("变化: %.1fkg (%s)", 
                    Math.abs(change), change > 0 ? "上升" : "下降"));
            }
        }

        // 3. 构建用户偏好描述
        StringBuilder preference = new StringBuilder();
        if (intensity != null && !intensity.isEmpty()) {
            switch (intensity) {
                case "high": preference.append("强度：提高。"); break;
                case "low": preference.append("强度：降低。"); break;
                default: preference.append("强度：适中。");
            }
        }
        if (duration != null && !duration.isEmpty()) {
            switch (duration) {
                case "long": preference.append("时长：延长。"); break;
                case "short": preference.append("时长：缩短。"); break;
                default: preference.append("时长：正常。");
            }
        }
        if (diet != null && !diet.isEmpty()) {
            switch (diet) {
                case "more_vege": preference.append("饮食：多素。"); break;
                case "more_meat": preference.append("饮食：多肉。"); break;
                default: preference.append("饮食：均衡。");
            }
        }
        if (preference.length() == 0) {
            preference.append("无特殊偏好。");
        }

        // 4. 构建简洁版Prompt（限制字数）
        String prompt = String.format(
            "你是健康管理师。根据以下信息生成简洁健康计划，**总字数控制在1200字以内**。\n\n" +
            "【用户】%d岁%s，%.0fcm，%.1fkg，%s，%s，目标%s\n" +
            "【体重】%s\n" +
            "【偏好】%s\n\n" +
            "【要求】\n" +
            "1. 核心分析（150字）：BMI、趋势、1-2条提醒\n" +
            "2. 饮食（表格）：周一至周日，每天三餐，每餐一句话\n" +
            "3. 运动（表格）：周一至周日，每天一项，类型+时长+强度\n" +
            "4. 建议（100字）：3条具体建议\n" +
            "5. Markdown格式，简洁，不写原理和目的",
            profile.getAge(), profile.getGender(), profile.getHeight(), profile.getWeight(),
            profile.getActivityLevel(), profile.getDietPref(), profile.getGoal(),
            weightContext.toString(),
            preference.toString()
        );

        // 5. 调用DeepSeek API或返回模拟数据
        String response = callDeepSeek(prompt);

        // 6. 截断保护
        if (response.length() > 2500) {
            response = response.substring(0, 2500) + "\n\n...（内容已截断）";
        }

        // 7. 保存到历史记录
        PlanHistory history = new PlanHistory();
        history.setUserId(userId);
        history.setPlanType("weekly");
        history.setContent(response);
        history.setWeightContext(weightContext.toString() + "\n偏好：" + preference.toString());
        planHistoryMapper.insert(history);

        // 8. 记录本次调用时间
        lastCallTime.put(userId, now);

        return response;
    }

    private String callDeepSeek(String prompt) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty() || 
            config.getApiKey().equals("sk-your-api-key-here")) {
            return generateMockPlan(prompt);
        }

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        Map<String, Object> body = Map.of(
            "model", config.getModel(),
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "temperature", 0.7,
            "max_tokens", 2500
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = rest.postForObject(config.getApiUrl(), request, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");
            System.out.println("=== DeepSeek 返回长度: " + content.length() + " 字符");
            return content;
        } catch (Exception e) {
            System.out.println("=== DeepSeek 调用失败: " + e.getMessage());
            return generateMockPlan(prompt);
        }
    }

    private String generateMockPlan(String prompt) {
        return "# 个性化健康计划\n\n" +
               "## 核心分析\n\n" +
               "**用户画像**：20岁男性，170cm，65kg，BMI 22.5（正常）。\n\n" +
               "**体重趋势**：近期稳定，建议适当增加肌肉量。\n\n" +
               "**关键提醒**：\n" +
               "1. 每日蛋白质摄入不低于1.2g/kg体重\n" +
               "2. 注意维生素D和钙的补充\n\n" +
               "## 本周饮食\n\n" +
               "| 日期 | 早餐 | 午餐 | 晚餐 |\n" +
               "|------|------|------|------|\n" +
               "| 周一 | 燕麦+鸡蛋+牛奶 | 鸡胸+糙米+西兰花 | 清蒸鱼+菠菜 |\n" +
               "| 周二 | 全麦面包+花生酱 | 牛肉+杂粮饭 | 豆腐汤+黄瓜 |\n" +
               "| 周三 | 酸奶+坚果 | 三文鱼+藜麦 | 蔬菜鸡肉丸 |\n" +
               "| 周四 | 小米粥+鸡蛋 | 鸡腿+红薯 | 瘦肉+彩椒 |\n" +
               "| 周五 | 三明治+豆浆 | 金枪鱼沙拉 | 冬瓜排骨 |\n" +
               "| 周六 | 紫薯+鸡蛋 | 外食清淡 | 魔芋丝汤 |\n" +
               "| 周日 | 馒头+豆浆 | 烤三文鱼 | 番茄鸡蛋汤 |\n\n" +
               "## 本周运动\n\n" +
               "| 日期 | 运动 | 时长 | 强度 |\n" +
               "|------|------|------|------|\n" +
               "| 周一 | HIIT | 30min | 高 |\n" +
               "| 周二 | 力量训练 | 45min | 中-高 |\n" +
               "| 周三 | 快走 | 30min | 低 |\n" +
               "| 周四 | 力量训练 | 50min | 高 |\n" +
               "| 周五 | HIIT | 35min | 高 |\n" +
               "| 周六 | 趣味运动 | 60min | 中 |\n" +
               "| 周日 | 休息 | - | - |\n\n" +
               "## 调整建议\n\n" +
               "1. 保证7-8小时睡眠\n" +
               "2. 每日饮水2升\n" +
               "3. 训练后30分钟内补充蛋白质";
    }
}
