package org.example.controller;

import org.example.service.StatisticsService;
import org.example.vo.LotteryStatisticsVO;
import org.example.vo.QuizStatisticsVO;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 答题统计
     * 请求：POST /statistics/quiz  Body = {"date":"2025-07-13"}
     */
    @PostMapping("/quiz")
    public Result quizStatistics(@RequestBody Map<String,String> params) {
        String date = params.get("date");
        QuizStatisticsVO vo = statisticsService.getQuizStatistics(date);
        return Result.ok(vo, "查询成功");
    }

    /**
     * 抽奖统计
     * 请求：POST /statistics/lottery  Body = {"date":"2025-07-13"}
     */
    @PostMapping("/lottery")
    public Result lotteryStatistics(@RequestBody Map<String,String> params) {
        String date = params.get("date");
        LotteryStatisticsVO vo = statisticsService.getLotteryStatistics(date);
        return Result.ok(vo, "查询成功");
    }
}
