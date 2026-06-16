package com.hnust.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("plan_history")
public class PlanHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String planType;
    private String content;
    private String weightContext;
    private LocalDateTime generatedAt;
}
