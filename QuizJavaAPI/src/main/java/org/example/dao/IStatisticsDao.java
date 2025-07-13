package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface IStatisticsDao {

    // —— Quiz 统计 ——
    @Select("SELECT COUNT(*) FROM quiz_records WHERE DATE(submit_time) = #{date}")
    int countTotalQuiz(@Param("date") String date);

    @Select("SELECT u.company AS company, COUNT(*) AS count "
            + "FROM quiz_records r "
            + "JOIN users u ON r.user_id = u.id "
            + "WHERE DATE(r.submit_time) = #{date} "
            + "GROUP BY u.company")
    List<Map<String,Object>> countQuizByCompany(@Param("date") String date);

    @Select("SELECT u.company       AS company, "
            + "       u.nickname      AS nickname, "
            + "       r.score         AS score, "
            + "       r.time_used     AS timeUsed, "
            + "       r.submit_time   AS submitTime "
            + "FROM quiz_records r "
            + "JOIN users u ON r.user_id = u.id "
            + "WHERE DATE(r.submit_time) = #{date}")
    List<Map<String,Object>> findQuizRecordsByDate(@Param("date") String date);

    // —— Lottery 统计 ——
    @Select("SELECT COUNT(*) FROM lottery_records WHERE DATE(draw_time) = #{date}")
    int countTotalLottery(@Param("date") String date);

    @Select("SELECT u.company AS company, COUNT(*) AS count "
            + "FROM lottery_records l "
            + "JOIN users u ON l.user_id = u.id "
            + "WHERE DATE(l.draw_time) = #{date} "
            + "GROUP BY u.company")
    List<Map<String,Object>> countLotteryByCompany(@Param("date") String date);

    @Select("SELECT u.company       AS company, "
            + "       u.nickname      AS nickname, "
            + "       l.prize_name    AS prizeName, "
            + "       l.prize_amt     AS prizeAmt, "
            + "       l.draw_time     AS drawTime "
            + "FROM lottery_records l "
            + "JOIN users u ON l.user_id = u.id "
            + "WHERE DATE(l.draw_time) = #{date}")
    List<Map<String,Object>> findLotteryRecordsByDate(@Param("date") String date);
}
