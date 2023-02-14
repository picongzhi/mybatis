package com.pcz.mybatis.core.builder.mapper;

import com.pcz.mybatis.core.builder.domain.User;

public interface UserMapper {
    User selectAllUsers();

    User selectUserById(Integer id);
}
