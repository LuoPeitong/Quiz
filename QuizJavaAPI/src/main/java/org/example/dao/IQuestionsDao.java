package org.example.dao;

import org.apache.ibatis.annotations.Select;
import org.example.model.Questions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuestionsDao {

    @Select("SELECT * FROM questions ORDER BY RAND() LIMIT #{limit}")
    List<Questions> getRandomQuestions(int limit);

    // 新增：根据题目 ID 查询答案
    @Select("SELECT answer FROM questions WHERE id = #{id}")
    String getAnswerById(int id);
}
