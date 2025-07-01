package org.example.service.impl;

import org.example.dao.IUsersDao;
import org.example.model.Users;
import org.example.service.UsersService;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service("usersService")
public class UsersServiceImpl implements UsersService {

    @Autowired
    private IUsersDao iUsersDao;

    public Result login(Users users, HttpSession session){

        Users u = iUsersDao.getUserById(users.getPhone());

        if (u == null){
            users.setGender("男");
            iUsersDao.register(users);
        }
        u = iUsersDao.getUserById(users.getPhone());
        session.setAttribute("login_user_id",u);
        return Result.ok(u,"缓存成功");
    }
}
