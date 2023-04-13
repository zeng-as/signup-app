package com.as.signup.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class SignupRecord {
    private Integer id;

    private Integer classesId;

    private String mobile;

    private String name;

    private String organization;

    private String post;

    private String area;

    private String email;

    private String files;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
