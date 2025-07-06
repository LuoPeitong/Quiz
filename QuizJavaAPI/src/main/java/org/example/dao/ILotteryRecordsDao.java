package org.example.dao;

import org.apache.ibatis.annotations.Insert;
import org.example.model.LotteryRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ILotteryRecordsDao {

    @Insert("INSERT INTO lottery_records (user_id, prize_id, prize_name, prize_amt, draw_time) VALUES (#{userId}, #{prizeId}, #{prizeName}, #{prizeAmt}, #{drawTime})")
    int addLotteryRecords(LotteryRecord lotteryRecord);

}
