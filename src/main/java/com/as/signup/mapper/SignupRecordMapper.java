package com.as.signup.mapper;

import com.as.signup.entity.SignupRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SignupRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SignupRecord record);

    int insertSelective(SignupRecord record);

    SignupRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SignupRecord record);

    int updateByPrimaryKey(SignupRecord record);

    List<SignupRecord> selectByMobile(String mobile);

    List<SignupRecord> selectByMobileAndClasses(@Param("mobile") String mobile, @Param("classesId") Integer classesId);

    List<SignupRecord> selectAll();

    List<SignupRecord> selectByClassesId(Integer classesId);
}
