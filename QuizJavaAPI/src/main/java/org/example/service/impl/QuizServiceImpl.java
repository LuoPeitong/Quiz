package org.example.service.impl;

import org.example.dao.IQuestionsDao;
import org.example.dao.IQuizAnswersDao;
import org.example.dao.IQuizRecordsDao;
import org.example.model.LotteryPrize;
import org.example.model.Questions;
import org.example.model.QuizAnswers;
import org.example.model.QuizRecords;
import org.example.service.LotteryService;
import org.example.service.QuizService;
import org.example.vo.OptionDTO;
import org.example.vo.Result;
import org.example.vo.SlideQuestionDTO;
import org.example.vo.SubmitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("quizService")
public class QuizServiceImpl implements QuizService {

    @Autowired
    private IQuestionsDao iQuestionsDao;

    @Autowired
    private IQuizRecordsDao iQuizRecordsDao;

    @Autowired
    private IQuizAnswersDao iQuizAnswersDao;

    @Autowired
    private LotteryService lotteryService;

    public Result getQuestions(){

        // 1. 从数据库拉原始题目
        List<Questions> raw = iQuestionsDao.getRandomQuestions(5);

        // 2. 转换成插件需要的格式
        List<SlideQuestionDTO> slideList = new ArrayList<>(raw.size());
        for (Questions q : raw) {
            SlideQuestionDTO dto = new SlideQuestionDTO();
            dto.setId(String.valueOf(q.getId()));
            dto.setTitle(q.getQuestion());
            dto.setAnswer(q.getAnswer());
            // 映射题型
            String pt = "QUESTION";
            if ("选择题".equals(q.getType())||"判断题".equals(q.getType())) {
                pt = "SINGLE";
            } else if ("多选题".equals(q.getType())) {
                pt = "MULTY";
            }
            dto.setProblemType(pt);

            // 单/多选题，生成 children
            if (!"QUESTION".equals(pt)) {
                List<String> opts = q.getOptions();  // Questions.setOptions 已经把 JSON 转成 List<String>
                List<OptionDTO> children = new ArrayList<>(opts.size());
                char alias = 'A';
                for (String opt : opts) {
                    OptionDTO child = new OptionDTO();
                    child.setAlias(String.valueOf(alias++));
                    // 拆分 "A. 文本" 或 "A、文本"
                    String answerText;
                    int splitIdx = opt.indexOf(' ');
                    if (splitIdx > 1) {
                        answerText = opt.substring(splitIdx + 1).trim();
                    } else {
                        // fallback：删除前两个字符
                        answerText = opt.length() > 2 ? opt.substring(2).trim() : opt;
                    }
                    child.setAnswer(answerText);
                    children.add(child);
                }
                dto.setChildren(children);
            }
            // 简答题默认 userAnswer = ""
            slideList.add(dto);
        }

        return Result.ok(slideList, "题目获取成功");
    }

    public Result submitQuizRecords(SubmitDTO dto) {
        // 1. 插入 quiz_records，并回填自增主键
        QuizRecords record = new QuizRecords();
        record.setUserId(dto.getUserId());
        record.setTimeUsed(dto.getTimeUsed());
        record.setScore(0);
        iQuizRecordsDao.insertQuizRecord(record);

        // 2. 构造答案 & 计分
        int totalScore = 0;
        List<QuizAnswers> answerEntities = new ArrayList<>();

        for (SlideQuestionDTO q : dto.getDataList()) {
            String userAns = "";
            System.out.println(q);
// 2.1 从 userAnswer 获取原始值，可能是 Map 或 List
            Object rawAns = q.getUserAnswer();

// 2.2 统一组装成 List<OptionDTO>
            List<OptionDTO> optionDTOS = new ArrayList<>();
            if (rawAns instanceof List) {
                for (Object item : (List<?>) rawAns) {
                    if (item instanceof OptionDTO) {
                        optionDTOS.add((OptionDTO) item);
                    } else if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) item;
                        OptionDTO od = new OptionDTO();
                        od.setAlias((String) map.get("alias"));
                        od.setAnswer((String) map.get("answer"));
                        Object sel = map.get("isSelect");
                        od.setIsSelect(sel instanceof Number
                                ? ((Number) sel).intValue()
                                : sel instanceof Boolean
                                ? ((Boolean) sel ? 1 : 0)
                                : 0
                        );
                        optionDTOS.add(od);
                    }
                }
            } else if (rawAns instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) rawAns;
                OptionDTO od = new OptionDTO();
                od.setAlias((String) map.get("alias"));
                od.setAnswer((String) map.get("answer"));
                Object sel = map.get("isSelect");
                od.setIsSelect(sel instanceof Number
                        ? ((Number) sel).intValue()
                        : sel instanceof Boolean
                        ? ((Boolean) sel ? 1 : 0)
                        : 0
                );
                optionDTOS.add(od);
            }

            // 构造 StringBuilder
            StringBuilder sb = new StringBuilder();
            // 拼接每个选项的 alias 和逗号
            for (OptionDTO o : optionDTOS) {
                System.out.println(o.getAlias());
                sb.append(o.getAlias()).append(',');
            }
            // 如果有内容，就去掉最后一个多余的逗号
            if (sb.length() > 1) {
                sb.setLength(sb.length() - 1);
            }
            // 转成最终的 userAns 字符串
            userAns = sb.toString();
            System.out.println(userAns);

            // 3. 拿正确答案、拆分、排序
            String correct = iQuestionsDao.getAnswerById(Integer.valueOf(q.getId()));

            boolean isCorrect = userAns.equals(correct);
            // 4. 构造 QuizAnswers 实体
            QuizAnswers ans = new QuizAnswers();
            ans.setRecordId(record.getId());
            ans.setQuestionId(Integer.valueOf(q.getId()));
            ans.setUserAnswer(userAns);
            ans.setIsCorrect(isCorrect ? 1 : 0);
            answerEntities.add(ans);

            if (isCorrect) {
                totalScore += 20;  // 每题 5 分
            }
        }

        // 5. 批量写入 answers & 更新总分
        iQuizAnswersDao.batchInsertAnswers(answerEntities);
        iQuizRecordsDao.updateScore(record.getId(), totalScore);

//        // 6. 返回给前端：包含 score 和 recordId
//        Map<String, Object> resp = new HashMap<>();
//        resp.put("score", totalScore);
//        resp.put("recordId", record.getId());

        LotteryPrize lotteryPrize = new LotteryPrize();
        if(totalScore==100){
            lotteryPrize = lotteryService.draw(dto.getUserId());
            //System.out.println("可以抽奖");
        }
        return Result.ok(lotteryPrize, "提交成功");
    }
}
