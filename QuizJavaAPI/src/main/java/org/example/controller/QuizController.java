package org.example.controller;

import org.example.service.QuizService;
import org.example.vo.Result;
import org.example.vo.SubmitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;

    @RequestMapping("/api/questions/random")
    public Result getQuestions(){

        try {
            return quizService.getQuestions();

        } catch (Exception e) {
            System.out.println("/api/questions/random: " + e);
            return Result.error();
        }
    }

    @RequestMapping(value = "/api/submit")
    public Result submit(@RequestBody SubmitDTO submitDTO){

        try {
            return quizService.submitQuizRecords(submitDTO);

        } catch (Exception e) {
            System.out.println("/api/submit: " + e);
            return Result.error();
        }
    }

    @PostMapping("/dashboard/getQuizStats")
    public Result getQuizStats(@RequestBody Map<String, String> params) {
        String dateStr = params.get("date");
        try {
            return quizService.getQuizStats(dateStr);
        } catch (Exception e) {
            System.err.println("/api/dashboard/getQuizStats: " + e);
            return Result.error("获取统计数据失败");
        }
    }

    /** 1. 下拉：获取所有公司 */
    @GetMapping("/dashboard/getAllCompanies")
    public Result getAllCompanies() {
        try {
            return quizService.getAllCompanies();
        } catch (Exception e) {
            return Result.error("获取公司列表失败");
        }
    }

    /** 2. 查询指定公司员工统计 */
    @PostMapping("/dashboard/getCompanyEmployeeStats")
    public Result getCompanyEmployeeStats(@RequestBody Map<String, String> params) {
        try {
            return quizService.getCompanyEmployeeStats(params.get("company"));
        } catch (Exception e) {
            return Result.error("获取员工统计失败");
        }
    }

    /** 3. 排名明细带分页 */
    @PostMapping("/dashboard/getEmployeeRankList")
    public Result getEmployeeRankList(@RequestBody Map<String, Object> params) {
        try {
            String sortField = (String) params.get("sortField");
            int page        = (int) params.get("page");
            int pageSize    = (int) params.get("pageSize");
            return quizService.getEmployeeRankList(sortField, page, pageSize);
        } catch (Exception e) {
            return Result.error("获取排名列表失败");
        }
    }
}
