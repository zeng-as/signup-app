package com.as.signup.entity;

import lombok.Data;
import lombok.ToString;

import javax.annotation.Generated;
import java.util.Date;

@Data
@ToString
public class Classes {

    private Integer id;

    private String name;

    private Date signupStart;

    private Date signupEndtime;

    private Integer maxNum;

    private Integer currentNum;

    private String wxQrcode;

    private Date createTime;

    private Date updateTime;

}