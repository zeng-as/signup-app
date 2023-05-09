package com.as.signup.service;

import com.as.signup.entity.User;
import com.as.signup.mapper.SignupRecordMapper;
import com.as.signup.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SignupRecordMapper signupRecordMapper;

    public void loginRecord(String userName) {
        Date now = new Date();
        User user = userMapper.selectByUserName(userName);
        if (null == user) {
            return;
        }

        user.setLastLoginTime(now);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public boolean checkPassword(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            return false;
        }

        User user = userMapper.selectByUserName(userName);
        if (null == user) {
            return false;
        }

        return password.equals(user.getPassword());
    }

    public User register(String userName) {
        User user = userMapper.selectByUserName(userName);
        if (null == user) {
            user = new User();
            user.setUserName(userName);
            // 默认截取userName后4位加上 !@#$
            int l = userName.length();
            String pwd = userName.substring(l <= 4 ? 0 : l - 4, l);
            pwd += "!@#$";
            user.setPassword(pwd);
            Date now = new Date();
            user.setCreateTime(now);
            user.setUpdateTime(now);

            userMapper.insertSelective(user);
        }

        return user;
    }

    public void modifyPwd(String userName, String pwd) {
        User user = userMapper.selectByUserName(userName);
        if (null == user) {
            return;
        }

        user.setPassword(pwd);
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);
    }

    public void initUsers(Integer period) {
        List<String> mobiles = signupRecordMapper.selectMobilesByPeriod(period);
        for (String mobile : mobiles) {
            this.register(mobile);
        }
    }
}
