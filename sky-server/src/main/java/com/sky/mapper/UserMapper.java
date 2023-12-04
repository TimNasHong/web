package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select*from user where openid=#{openID}")
    User selectOrNot(String openID);

    void insertUser(User userNew);
@Select("select*from user where id=#{userId}")
    User getById(Long userId);
}
