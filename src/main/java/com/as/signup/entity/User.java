package com.as.signup.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class User {
    private Integer id;

    private String userName;

    private String password;

    private Date lastLoginTime;

    private Date createTime;

    private Date updateTime;
}