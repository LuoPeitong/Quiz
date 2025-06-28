package org.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizRecords {
    private Integer id;
    private Integer userId;
    private LocalDateTime submitTime;
    private Integer score;
    private Integer timeUsed;

}
