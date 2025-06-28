package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuizAnswers {
    private Integer id;

    @JsonProperty("record_id")
    private Integer recordId;

    @JsonProperty("question_id")
    private Integer questionId;

    @JsonProperty("user_answer")
    private String userAnswer;

    @JsonProperty("is_correct")
    private Integer isCorrect;  // 建议用 Integer 对应 tinyint(1)

}
