package com.as.signup.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Logs {
    private Integer id;

    private String request;

    private String response;

    private Date reqTime;

    private Date respTime;

    private Date createTime;

}