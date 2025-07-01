package org.example.service;

import org.example.vo.Result;

import javax.servlet.http.HttpSession;

public interface HomeService {

    Result getRank();

    Result initPage(HttpSession session);
}
