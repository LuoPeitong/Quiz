package org.example.dao;

import org.apache.ibatis.annotations.Select;
import org.example.model.LotteryPrize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILotteryPrizeDao {

    @Select("SELECT id, name, amount, weight FROM lottery_prizes order by id")
    List<LotteryPrize> findAll();

}
