package com.hnust.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnust.health.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}
