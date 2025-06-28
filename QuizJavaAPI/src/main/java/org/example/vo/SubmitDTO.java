// org/example/vo/SubmitDTO.java
package org.example.vo;

import lombok.Data;
import java.util.List;

@Data
public class SubmitDTO {

    private Integer userId;
    private Integer timeUsed;
    // 插件原始数据
    private List<SlideQuestionDTO> dataList;
}
