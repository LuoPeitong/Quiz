package org.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LotteryRecord {

    private Integer id;
    private Integer userId;
    private Integer prizeId;
    private String prizeName;
    private Integer prizeAmt;
    private LocalDateTime drawTime;
}
