package org.example.service.impl;

import org.example.dao.IStatisticsDao;
import org.example.service.StatisticsService;
import org.example.vo.LotteryStatisticsVO;
import org.example.vo.QuizStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private IStatisticsDao statisticsDao;

    @Override
    public QuizStatisticsVO getQuizStatistics(String date) {
        // 1. 总数
        int total = statisticsDao.countTotalQuiz(date);

        // 2. 分公司统计
        List<Map<String,Object>> compList = statisticsDao.countQuizByCompany(date);

        // 3. 个人详情
        List<Map<String,Object>> userList = statisticsDao.findQuizRecordsByDate(date);

        // 构造 VO
        QuizStatisticsVO vo = new QuizStatisticsVO();
        QuizStatisticsVO.Summary summary = new QuizStatisticsVO.Summary();
        summary.setTotal(total);
        vo.setSummary(summary);

        List<QuizStatisticsVO.CompanyStat> csList = new ArrayList<>();
        for (Map<String,Object> m : compList) {
            QuizStatisticsVO.CompanyStat cs = new QuizStatisticsVO.CompanyStat();
            cs.setCompany( (String) m.get("company") );
            cs.setCount( ((Number)m.get("count")).intValue() );
            csList.add(cs);
        }
        vo.setCompanyStats(csList);

        List<QuizStatisticsVO.UserStat> usList = new ArrayList<>();
        for (Map<String,Object> m : userList) {
            QuizStatisticsVO.UserStat us = new QuizStatisticsVO.UserStat();
            us.setCompany(    (String) m.get("company") );
            us.setNickname(   (String) m.get("nickname") );
            us.setScore(      ((Number)m.get("score")).intValue() );
            us.setTimeUsed(   ((Number)m.get("timeUsed")).intValue() );
            us.setSubmitTime( m.get("submitTime").toString() );
            usList.add(us);
        }
        vo.setUserStats(usList);

        return vo;
    }

    @Override
    public LotteryStatisticsVO getLotteryStatistics(String date) {
        int total = statisticsDao.countTotalLottery(date);
        List<Map<String,Object>> compList = statisticsDao.countLotteryByCompany(date);
        List<Map<String,Object>> userList = statisticsDao.findLotteryRecordsByDate(date);

        LotteryStatisticsVO vo = new LotteryStatisticsVO();
        LotteryStatisticsVO.Summary summary = new LotteryStatisticsVO.Summary();
        summary.setTotalWinners(total);
        vo.setSummary(summary);

        List<LotteryStatisticsVO.CompanyWinner> cwList = new ArrayList<>();
        for (Map<String,Object> m : compList) {
            LotteryStatisticsVO.CompanyWinner cw = new LotteryStatisticsVO.CompanyWinner();
            cw.setCompany((String)m.get("company"));
            cw.setCount(((Number)m.get("count")).intValue());
            cwList.add(cw);
        }
        vo.setCompanyWinners(cwList);

        List<LotteryStatisticsVO.UserWinner> uwList = new ArrayList<>();
        for (Map<String,Object> m : userList) {
            LotteryStatisticsVO.UserWinner uw = new LotteryStatisticsVO.UserWinner();
            uw.setCompany(   (String) m.get("company") );
            uw.setNickname(  (String) m.get("nickname") );
            uw.setPrizeName((String) m.get("prizeName") );
            uw.setPrizeAmt( ((Number)m.get("prizeAmt")).intValue() );
            uw.setDrawTime( m.get("drawTime").toString() );
            uwList.add(uw);
        }
        vo.setUserWinners(uwList);

        return vo;
    }
}
