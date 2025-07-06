package org.example.dao;

import org.apache.ibatis.annotations.*;
import org.example.model.QuizRecords;
import org.example.vo.RankDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            "        ROW_NUMBER() OVER (PARTITION BY r.user_id ORDER BY r.score DESC, r.time_used ASC) AS rn ",
            "    FROM quiz_records r ",
            ") t ",
            "JOIN users u ON t.user_id = u.id ",
            "WHERE t.rn = 1 ",
            "ORDER BY t.score DESC ",
            "LIMIT 30 "
    })
    List<RankDTO> getTopRank();
}
