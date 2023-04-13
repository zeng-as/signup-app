package com.as.signup.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class File {
    private Integer id;

    private String url;

    private Date createTime;
}