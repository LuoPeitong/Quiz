package org.example.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlideQuestionDTO {
    private String id;               // 插件示例中用字符串
    private String title;            // 题干
    private String problemType;      // SINGLE / MULTY / QUESTION
    private List<OptionDTO> children; // 选项，单/多选题用
    private Object userAnswer;  // 简答/填空题用
    private String answer;
}
