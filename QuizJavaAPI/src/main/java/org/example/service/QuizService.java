package org.example.service;

import org.example.vo.Result;
import org.example.vo.SubmitDTO;

public interface QuizService {

    Result getQuestions();

    Result submitQuizRecords(SubmitDTO submitDTO);
}
