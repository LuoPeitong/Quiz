package org.example.service;

import org.example.model.Users;
import org.example.vo.Result;


public interface HomeService {

    Result getRank();

    Result initPage(Users u);
}
