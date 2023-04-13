package com.as.signup.dto;

import com.as.signup.entity.SignupRecord;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SignupDTO extends SignupRecord {

    private List<Integer> classes;
    // 缴费证明
    private List<Integer> fileIds;
    // 免费证明
    private List<Integer> file2Ids;
}
