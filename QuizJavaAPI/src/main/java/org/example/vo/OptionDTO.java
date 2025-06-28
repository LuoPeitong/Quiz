package org.example.vo;

import lombok.Data;

@Data
public class OptionDTO {
    private String alias;    // A、B、C…
    private String answer;   // 选项文本
    private Integer isSelect = 0; // 前端回显用，0 未选中，1 已选中
}
