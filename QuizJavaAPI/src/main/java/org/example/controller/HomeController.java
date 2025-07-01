package org.example.controller;

import org.example.model.Users;
import org.example.service.HomeService;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @ResponseBody
    @PostMapping("/api/init")
    public Result initPage(@RequestBody Users u) {

        try {
            System.out.println(u.getId());
            return homeService.initPage(u);
        } catch (Exception e) {
            System.out.println("api/init: " + e);
            return Result.error();
        }
    }
}
