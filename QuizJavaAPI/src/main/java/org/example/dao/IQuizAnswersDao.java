package org.example.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.example.model.QuizAnswers;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuizAnswersDao {

    @Insert("INSERT INTO quiz_answers (record_id, question_id, user_answer, is_correct) VALUES (#{recordId}, #{questionId}, #{userAnswer}, #{isCorrect})" )
    int batchInsertAnswers1(QuizAnswers answers);

    @Insert({
            "<script>",
            "INSERT INTO quiz_answers (record_id, question_id, user_answer, is_correct) VALUES ",
            "<foreach collection='answers' item='item' separator=','>",
            "(#{item.recordId}, #{item.questionId}, #{item.userAnswer}, #{item.isCorrect})",
            "</foreach>",
            "</script>"
    })
    int batchInsertAnswers(@Param("answers") List<QuizAnswers> answers);
}
