package com.as.signup.mapper;

import com.as.signup.entity.Logs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Logs record);

    int insertSelective(Logs record);

    Logs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Logs record);

    int updateByPrimaryKey(Logs record);
}