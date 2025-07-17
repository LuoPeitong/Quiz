package org.example.service.impl;

import org.example.dao.ICompanyDao;
import org.example.dao.ILotteryPoolDao;
import org.example.dao.IQuizRecordsDao;
import org.example.model.LotteryPool;
import org.example.model.Users;
import org.example.service.HomeService;
import org.example.vo.RankDTO;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("homeService")
public class HomeServiceImpl implements HomeService {

    private static final Integer DAILY_IN = new Integer("3000");

    @Autowired
    private IQuizRecordsDao iQuizRecordsDao;

    @Autowired
    private ILotteryPoolDao iLotteryPoolDao;

    @Autowired
    private ICompanyDao iCompanyDao;

    public Result getRank(){

        return Result.ok(iQuizRecordsDao.getTopRank(), "排行榜获取成功");
    }

    public Result initPage(Users u){

        //Users u = (Users) session.getAttribute("login_user_id");

        int userID = u.getId();

        List<RankDTO> rankDTOS = iQuizRecordsDao.getTopRank();
        int countAll = iQuizRecordsDao.countTotalAttempts();
        LocalDate today = LocalDate.now();                      // 动态取当前日期
        LocalDateTime start = today.atStartOfDay();             // 今天 00:00:00
        LocalDateTime end   = today.plusDays(1).atStartOfDay(); // 明天 00:00:00
        int todayCount = iQuizRecordsDao.countTodayAttempts(userID,start,end);

        LotteryPool lot = iLotteryPoolDao.findById(today);
        if(lot == null){
            lot = new LotteryPool();
            lot.setPoolDate(today);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LotteryPool yesterday_pool = iLotteryPoolDao.findById(yesterday);
            if(yesterday_pool!=null){

                lot.setBalance(DAILY_IN + yesterday_pool.getBalance());
            }
            else{
                lot.setBalance(DAILY_IN);
            }

            iLotteryPoolDao.init(lot);
        }
        int todayBalance = lot.getBalance();

        Map<String, Object> stats = new HashMap<>();
        stats.put("participantCount", countAll);
        stats.put("remainingChance", todayCount);
        stats.put("otherRanks", rankDTOS);
        stats.put("todayBalance", todayBalance);

        return Result.ok(stats, "查询成功");
    }

    public Result getCompany(){

        List<String> names = iCompanyDao.getAllCompany();
        return Result.ok(names, "获取成功");
    }
}
