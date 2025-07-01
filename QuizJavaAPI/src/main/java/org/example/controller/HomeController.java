package org.example.controller;

import org.example.service.HomeService;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@CrossOrigin
@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @ResponseBody
    @RequestMapping("/api/init")
    public Result initPage(HttpSession session) {

        try {

            return homeService.initPage(session);
        } catch (Exception e) {
            System.out.println("api/init: " + e);
            return Result.error();
        }
    }
}
