package org.example.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LotteryPool {

    private LocalDate poolDate;
    private Integer balance;
}
