package org.example.service.impl;

import org.example.dao.IQuizRecordsDao;
import org.example.service.HomeService;
import org.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("homeService")
public class HomeServiceImpl implements HomeService {

    @Autowired
    private IQuizRecordsDao iQuizRecordsDao;

    public Result getRank(){

        return Result.ok(iQuizRecordsDao.getTopRank(), "排行榜获取成功");
    }
}
