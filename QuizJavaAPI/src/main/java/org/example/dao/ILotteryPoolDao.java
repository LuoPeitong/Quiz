package org.example.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.model.LotteryPool;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ILotteryPoolDao {

    @Select("select * from lottery_pool where pool_date = #{today}")
    LotteryPool findById(LocalDate today);

    @Insert("insert into lottery_pool (pool_date, balance) values (#{poolDate}, #{balance})")
    int init(LotteryPool pool);

    @Update("UPDATE lottery_pool SET balance = #{balance} WHERE pool_date = #{poolDate}")
    int update(LotteryPool pool);
}
