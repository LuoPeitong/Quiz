package org.example.service.impl;

import org.example.dao.IQuizRecordsDao;
import org.example.model.Users;
import org.example.service.HomeService;
import org.example.vo.RankDTO;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("homeService")
public class HomeServiceImpl implements HomeService {

    @Autowired
    private IQuizRecordsDao iQuizRecordsDao;

    public Result getRank(){

        return Result.ok(iQuizRecordsDao.getTopRank(), "排行榜获取成功");
    }

    public Result initPage(HttpSession session){

        Users u = (Users) session.getAttribute("login_user_id");

        int userID = u.getId();

        List<RankDTO> rankDTOS = iQuizRecordsDao.getTopRank();
        int countAll = iQuizRecordsDao.countTotalAttempts();
        LocalDate today = LocalDate.now();                      // 动态取当前日期
        LocalDateTime start = today.atStartOfDay();             // 今天 00:00:00
        LocalDateTime end   = today.plusDays(1).atStartOfDay(); // 明天 00:00:00
        int todayCount = iQuizRecordsDao.countTodayAttempts(userID,start,end);

        Map<String, Object> stats = new HashMap<>();
        stats.put("participantCount", countAll);
        stats.put("remainingChance", todayCount);
        stats.put("otherRanks", rankDTOS);

        return Result.ok(stats, "查询成功");
    }
}
