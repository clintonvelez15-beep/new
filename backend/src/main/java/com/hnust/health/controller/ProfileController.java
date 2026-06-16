package com.hnust.health.controller;

import com.hnust.health.entity.UserProfile;
import com.hnust.health.mapper.UserProfileMapper;
import com.hnust.health.util.JwtUtil;
import com.hnust.health.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin
public class ProfileController {

    private final UserProfileMapper profileMapper;

    @PostMapping
    public Result<String> saveProfile(@RequestBody UserProfile profile, HttpServletRequest request) {
        Long userId = getUserId(request);
        profile.setUserId(userId);

        // 检查是否已存在
        UserProfile exist = profileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
        );
        if (exist != null) {
            profile.setId(exist.getId());
            profileMapper.updateById(profile);
        } else {
            profileMapper.insert(profile);
        }
        return Result.success("保存成功");
    }

    @GetMapping
    public Result<UserProfile> getProfile(HttpServletRequest request) {
        Long userId = getUserId(request);
        UserProfile profile = profileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
        );
        return Result.success(profile);
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.parseToken(token);
    }
}
