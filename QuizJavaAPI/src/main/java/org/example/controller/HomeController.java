package org.example.controller;

import org.example.service.HomeService;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin
@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    public void isLogin(){}

    @ResponseBody
    @RequestMapping("/api/getRank")
    public Result getRank(){

        try {
            return homeService.getRank();

        } catch (Exception e) {
            System.out.println(e);
            return Result.error();
        }
    }
}
