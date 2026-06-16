-- 创建数据库
CREATE DATABASE IF NOT EXISTS health_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE health_db;

-- 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 健康档案表
CREATE TABLE user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    age INT,
    gender VARCHAR(10),
    height DOUBLE,
    weight DOUBLE,
    activity_level VARCHAR(20),
    diet_pref VARCHAR(30),
    goal VARCHAR(30),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 体重记录表
CREATE TABLE weight_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    weight DOUBLE NOT NULL,
    record_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 计划历史表
CREATE TABLE plan_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(20),
    content TEXT,
    weight_context TEXT,
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
