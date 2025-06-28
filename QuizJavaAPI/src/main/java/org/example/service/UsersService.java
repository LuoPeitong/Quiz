package org.example.service;

import org.example.model.Users;
import org.example.vo.Result;

import javax.servlet.http.HttpSession;

public interface UsersService {

    Result login(Users users, HttpSession session);
}
