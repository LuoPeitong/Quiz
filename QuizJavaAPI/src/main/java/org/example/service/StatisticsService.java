package org.example.service;

import org.example.vo.LotteryStatisticsVO;
import org.example.vo.QuizStatisticsVO;

public interface StatisticsService {
    QuizStatisticsVO   getQuizStatistics(String date);
    LotteryStatisticsVO getLotteryStatistics(String date);
}
