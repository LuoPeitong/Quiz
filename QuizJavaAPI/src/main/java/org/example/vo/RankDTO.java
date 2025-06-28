package org.example.vo;

import lombok.Data;

@Data
public class RankDTO {

    private Integer userId;
    private String nickname;
    private String company;
    private String gender;
    private String timeUsed;
    private Integer score;
}
