package com.as.signup.controller;

import com.as.signup.common.R;
import com.as.signup.dto.LoginDTO;
import com.as.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.as.signup.common.CommonConstants.TOKEN_REDIS_KEY;
import static com.as.signup.common.CommonEnums.ResCode.LOGIN_FAIL;

@RestController
@RequestMapping("/log")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/in")
    public R<?> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        boolean ifSuccess = userService.checkPassword(loginDTO.getUserName(), loginDTO.getPassword());
        if (ifSuccess) {
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            Cookie ck = new Cookie("token", token);
            ck.setPath("/");
            response.addCookie(ck);

            String key = TOKEN_REDIS_KEY + token;
            stringRedisTemplate.opsForValue().set(key, loginDTO.getUserName(), 60, TimeUnit.MINUTES);

            userService.loginRecord(loginDTO.getUserName());
            return R.ok();
        } else {
            return R.getInstance(LOGIN_FAIL);
        }
    }
}
