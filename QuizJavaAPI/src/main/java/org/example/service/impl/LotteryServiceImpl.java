package org.example.service.impl;

import org.example.dao.ILotteryPoolDao;
import org.example.dao.ILotteryPrizeDao;
import org.example.dao.ILotteryRecordsDao;
import org.example.model.LotteryPool;
import org.example.model.LotteryPrize;
import org.example.model.LotteryRecord;
import org.example.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service("lotteryService")
public class LotteryServiceImpl implements LotteryService {

    private static final Integer DAILY_IN = new Integer("3000");

    @Autowired
    private ILotteryPoolDao iLotteryPoolDao;

    @Autowired
    private ILotteryPrizeDao iLotteryPrizeDao;

    @Autowired
    private ILotteryRecordsDao iLotteryRecordsDao;

    public LotteryPrize draw(Integer userId) {

        LocalDate today = LocalDate.now();
        LotteryPool pool = iLotteryPoolDao.findById(today);

        if(pool == null){

            pool = new LotteryPool();
            pool.setPoolDate(today);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LotteryPool yesterday_pool = iLotteryPoolDao.findById(yesterday);
            if(yesterday_pool!=null){

                pool.setBalance(DAILY_IN + yesterday_pool.getBalance());
            }
            else{
                pool.setBalance(DAILY_IN);
            }

            iLotteryPoolDao.init(pool);
        }

        if(pool.getBalance()<5){

            LotteryPrize nL = new LotteryPrize();
            nL.setId(-1);
            nL.setName("今日奖池已用完");
            return nL;
        }

        List<LotteryPrize> prizes = iLotteryPrizeDao.findAll();
        int totalWeight = prizes.stream().mapToInt(LotteryPrize::getWeight).sum();
        int rand = new Random().nextInt(totalWeight) + 1;
        LotteryPrize sel = prizes.get(prizes.size()-1);
        int cursor = 0;
        for (LotteryPrize p : prizes) {
            cursor += p.getWeight();
            if (rand <= cursor) {
                sel = p;
                break;
            }
        }

        LotteryRecord lotteryRecord = new LotteryRecord();
        lotteryRecord.setUserId(userId);
        lotteryRecord.setPrizeId(sel.getId());
        lotteryRecord.setPrizeName(sel.getName());
        lotteryRecord.setPrizeAmt(sel.getAmount());
        lotteryRecord.setDrawTime(LocalDateTime.now());
        iLotteryRecordsDao.addLotteryRecords(lotteryRecord);

        pool.setBalance(pool.getBalance()-sel.getAmount());
        iLotteryPoolDao.update(pool);
        return sel;
    }
}
