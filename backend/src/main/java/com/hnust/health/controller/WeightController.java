package com.hnust.health.controller;

import com.hnust.health.entity.WeightRecord;
import com.hnust.health.mapper.WeightRecordMapper;
import com.hnust.health.util.JwtUtil;
import com.hnust.health.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/weight")
@RequiredArgsConstructor
@CrossOrigin
public class WeightController {

    private final WeightRecordMapper weightRecordMapper;

    @PostMapping
    public Result<String> addWeight(@RequestBody WeightRecord record, HttpServletRequest request) {
        Long userId = getUserId(request);
        record.setUserId(userId);
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        weightRecordMapper.insert(record);
        return Result.success("记录成功");
    }

    @GetMapping("/history")
    public Result<List<WeightRecord>> getHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<WeightRecord> list = weightRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WeightRecord>()
                .eq(WeightRecord::getUserId, userId)
                .orderByDesc(WeightRecord::getRecordDate)
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
