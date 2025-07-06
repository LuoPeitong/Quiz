package org.example.service;

import org.example.model.LotteryPrize;

public interface LotteryService {

    LotteryPrize draw(Integer userId);
}
