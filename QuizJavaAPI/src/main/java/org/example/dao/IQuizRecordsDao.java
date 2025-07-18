package org.example.dao;

import org.apache.ibatis.annotations.*;
import org.example.model.QuizRecords;
import org.example.vo.RankDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface IQuizRecordsDao {

    @Update("UPDATE quiz_records SET score = #{score} WHERE id = #{recordId}")
    void updateScore(@Param("recordId") Integer recordId,
                     @Param("score") Integer score);

    @Insert("INSERT INTO quiz_records (user_id, score, time_used) VALUES (#{userId}, #{score}, #{timeUsed})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertQuizRecord(QuizRecords record);

    @Select("SELECT COUNT(*) FROM quiz_records")
    int countTotalAttempts();

    @Select("SELECT COUNT(*) FROM quiz_records WHERE user_id = #{userId} AND submit_time < #{end} AND submit_time >= #{start}")
    int countTodayAttempts(@Param("userId") int userId,
                           @Param("start")  java.time.LocalDateTime start,
                           @Param("end")    java.time.LocalDateTime end);

    @Select("SELECT * FROM quiz_records WHERE id = #{recordId}")
    QuizRecords getQuizRecordById(QuizRecords record);

    @Select("SELECT * FROM quiz_records WHERE user_id = #{userId} ORDER BY submit_time DESC")
    List<QuizRecords> getQuizRecordsByUser(QuizRecords record);

    @Select({
            "SELECT ",
            "    t.user_id, ",
            "    u.nickname, ",
            "    u.company, ",
            "    u.gender, ",
            "    t.score, ",
            "    t.time_used ",
            "FROM ( ",
            "    SELECT ",
            "        r.user_id, ",
            "        r.score, ",
            "        r.time_used, ",
            "        ROW_NUMBER() OVER (",
            "            PARTITION BY r.user_id ",
            "            ORDER BY r.score DESC, r.time_used ASC",
            "        ) AS rn ",
            "    FROM quiz_records r ",
            ") t ",
            "JOIN users u ON t.user_id = u.id ",
            "WHERE t.rn = 1 ",
            "ORDER BY t.score DESC, t.time_used ASC ",
            "LIMIT 30"
    })
    List<RankDTO> getTopRank();

    // 1. 各日期答题人数
    @Select("SELECT DATE(submit_time) AS date, COUNT(*) AS count " +
            "FROM quiz_records " +
            "GROUP BY DATE(submit_time) " +
            "ORDER BY DATE(submit_time)")
    List<Map<String, Object>> countParticipantsByDate();

    // 2. 指定日期下中奖分布
    @Select("SELECT prize_name AS name, COUNT(*) AS value " +
            "FROM lottery_records " +
            "WHERE DATE(draw_time) = #{date} " +
            "GROUP BY prize_name")
    List<Map<String, Object>> winnerDistributionByDate(@Param("date") LocalDate date);

    // 3. 公司答题次数前5
    @Select("SELECT u.company AS company, COUNT(*) AS count " +
            "FROM quiz_records r " +
            "JOIN users u ON r.user_id = u.id " +
            "GROUP BY u.company " +
            "ORDER BY count DESC " +
            "LIMIT 10")
    List<Map<String, Object>> top5CompanyStats();

    @Select("SELECT DISTINCT company FROM users")
    List<String> getAllCompanies();

    /** 2. 查询指定公司下各员工的答题及中奖次数统计 */
    @Select({
            "WITH quiz_count AS (",
            "    SELECT user_id, COUNT(*) AS participationCount",
            "    FROM quiz_records",
            "    GROUP BY user_id",
            "),",
            "lottery_count AS (",
            "    SELECT user_id,",
            "           SUM(CASE WHEN prize_name = '一等奖' THEN 1 ELSE 0 END) AS firstPrizeCount,",
            "           SUM(CASE WHEN prize_name = '二等奖' THEN 1 ELSE 0 END) AS secondPrizeCount,",
            "           SUM(CASE WHEN prize_name = '三等奖' THEN 1 ELSE 0 END) AS thirdPrizeCount,",
            "           SUM(CASE WHEN prize_name = '未中奖' THEN 1 ELSE 0 END) AS noPrizeCount",
            "    FROM lottery_records",
            "    GROUP BY user_id",
            ")",
            "SELECT",
            "    u.nickname AS name,",
            "    IFNULL(qc.participationCount, 0) AS participationCount,",
            "    IFNULL(lc.firstPrizeCount, 0) AS firstPrizeCount,",
            "    IFNULL(lc.secondPrizeCount, 0) AS secondPrizeCount,",
            "    IFNULL(lc.thirdPrizeCount, 0) AS thirdPrizeCount,",
            "    IFNULL(lc.noPrizeCount, 0) AS noPrizeCount",
            "FROM users u",
            "LEFT JOIN quiz_count qc ON qc.user_id = u.id",
            "LEFT JOIN lottery_count lc ON lc.user_id = u.id",
            "WHERE u.company = #{company}",
            "ORDER BY u.nickname"
    })
    List<Map<String, Object>> getCompanyEmployeeStats(@Param("company") String company);


    /** 3. 分页＋动态排序的员工排行榜明细 */
    @SelectProvider(type = QuizSqlProvider.class, method = "employeeRankList")
    List<Map<String,Object>> getEmployeeRankList(@Param("sortField") String sortField,
                                                 @Param("offset") int offset,
                                                 @Param("pageSize") int pageSize);

    /** 4. 员工排行榜总条数，用于分页 */
    @SelectProvider(type = QuizSqlProvider.class, method = "employeeRankCount")
    int countEmployeeRankList();
}
