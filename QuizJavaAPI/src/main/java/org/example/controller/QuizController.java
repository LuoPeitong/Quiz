package org.example.controller;

import org.example.service.QuizService;
import org.example.vo.Result;
import org.example.vo.SubmitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;

    @RequestMapping("/api/questions/random")
    public Result getQuestions(){

        try {
            return quizService.getQuestions();

        } catch (Exception e) {
            System.out.println(e);
            return Result.error();
        }
    }

    @RequestMapping(value = "/api/submit")
    public Result submit(@RequestBody SubmitDTO submitDTO){

        try {
            return quizService.submitQuizRecords(submitDTO);

        } catch (Exception e) {
            System.out.println(e);
            return Result.error();
        }
    }
}
