package com.hnust.health.controller;

import com.hnust.health.entity.User;
import com.hnust.health.mapper.UserMapper;
import com.hnust.health.util.JwtUtil;
import com.hnust.health.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserMapper userMapper;

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User exist = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername())
        );
        if (exist != null) {
            return Result.error("用户名已存在");
        }
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody User user) {
        User dbUser = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword())
        );
        if (dbUser == null) {
            return Result.error("用户名或密码错误");
        }
        String token = JwtUtil.generateToken(dbUser.getId());
        return Result.success(Map.of("token", token, "username", dbUser.getUsername()));
    }
}
