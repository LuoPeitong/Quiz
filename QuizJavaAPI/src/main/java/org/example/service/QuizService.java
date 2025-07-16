package org.example.service;

import org.example.vo.Result;
import org.example.vo.SubmitDTO;

public interface QuizService {

    Result getQuestions();

    Result submitQuizRecords(SubmitDTO submitDTO);

    Result getQuizStats(String dateStr);

    Result getAllCompanies();

    Result getCompanyEmployeeStats(String company);

    Result getEmployeeRankList(String sortField, int page, int pageSize);
}
