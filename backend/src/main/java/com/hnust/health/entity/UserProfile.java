package com.hnust.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer age;
    private String gender;
    private Double height;
    private Double weight;
    private String activityLevel;
    private String dietPref;
    private String goal;
    private LocalDateTime updatedAt;
}
