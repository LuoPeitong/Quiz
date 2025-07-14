package org.example.dao;

import org.apache.ibatis.annotations.Select;
import org.example.vo.Company;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICompanyDao {

    @Select("select company from company")
    List<String> getAllCompany();
}
