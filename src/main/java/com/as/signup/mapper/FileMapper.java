package com.as.signup.mapper;

import com.as.signup.entity.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(File record);

    int insertSelective(File record);

    File selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(File record);

    int updateByPrimaryKey(File record);
}