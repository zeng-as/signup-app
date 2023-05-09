package com.as.signup.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Classes {

    private Integer id;

    private Integer period;

    private String name;

    private Date signupStart;

    private Date signupEndtime;

    private Integer maxNum;

    private Integer currentNum;

    private String wxQrcode;

    private Date createTime;

    private Date updateTime;

    private String video;

    @JSONField(format = "yyyy-MM-dd")
    private Date videoValidStart;

    @JSONField(format = "yyyy-MM-dd")
    private Date videoValidEnd;

}