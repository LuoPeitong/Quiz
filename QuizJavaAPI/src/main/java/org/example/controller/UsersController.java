package org.example.controller;

import org.example.model.Users;
import org.example.service.UsersService;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public Result login(@RequestBody Users users, HttpSession session) {

        try{
            return usersService.login(users,session);
        }
        catch (Exception e) {
            return Result.error();
        }
    }

}
