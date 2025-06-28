package org.example.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.example.model.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsersDao {

    @Insert("INSERT INTO users (phone, nickname, company, gender) VALUES (#{phone}, #{nickname}, #{company}, #{gender})")
    int register(Users user);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    Users getUserById(String phone);
}
