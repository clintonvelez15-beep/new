package com.hnust.health.controller;

import com.hnust.health.entity.PlanHistory;
import com.hnust.health.entity.UserProfile;
import com.hnust.health.mapper.PlanHistoryMapper;
import com.hnust.health.mapper.UserProfileMapper;
import com.hnust.health.service.DeepSeekService;
import com.hnust.health.util.JwtUtil;
import com.hnust.health.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@CrossOrigin
public class PlanController {

    private final DeepSeekService deepSeekService;
    private final UserProfileMapper profileMapper;
    private final PlanHistoryMapper planHistoryMapper;

    @GetMapping("/generate")
    public Result<String> generatePlan(
            @RequestParam(required = false) String intensity,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String diet,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        UserProfile profile = profileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
        );
        if (profile == null) {
            return Result.error("请先完善健康档案");
        }
        String plan = deepSeekService.generatePlan(profile, userId, intensity, duration, diet);
        return Result.success(plan);
    }

    @GetMapping("/history")
    public Result<List<PlanHistory>> getHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<PlanHistory> list = planHistoryMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanHistory>()
                .eq(PlanHistory::getUserId, userId)
                .orderByDesc(PlanHistory::getGeneratedAt)
        );
        return Result.success(list);
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.parseToken(token);
    }
}
