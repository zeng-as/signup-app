package com.as.signup.controller;

import cn.hutool.core.util.ArrayUtil;
import com.as.signup.common.R;
import com.as.signup.common.UserThreadLocal;
import com.as.signup.dto.ModifyPwdReqDTO;
import com.as.signup.entity.Classes;
import com.as.signup.entity.User;
import com.as.signup.service.SignupService;
import com.as.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.as.signup.common.CommonConstants.CURRENT_ONLINE_PERIOD;
import static com.as.signup.common.CommonConstants.TOKEN_REDIS_KEY;
import static com.as.signup.common.CommonEnums.ResCode.PWD_ERROR;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SignupService signupService;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/getSignupClasses")
    public R<List<Classes>> getSignupClasses() {
        String mobile = UserThreadLocal.currentUser.get();
        return R.ok(signupService.getSignupClasses(mobile, CURRENT_ONLINE_PERIOD));
    }

    @PostMapping("/register/{userName}")
    public R<User> register(@PathVariable("userName") String userName) {
        return R.ok(userService.register(userName));
    }

    @PostMapping("/modifyPwd")
    public R<?> modifyPwd(@RequestBody ModifyPwdReqDTO reqDTO) {
        String mobile = UserThreadLocal.currentUser.get();
        userService.modifyPwd(mobile, reqDTO.getPwd());
        return R.ok();
    }

    @PostMapping("/initOnlineUsers")
    public R<?> initOnlineUsers() {
        userService.initUsers(CURRENT_ONLINE_PERIOD);
        return R.ok();
    }

    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        // 校验token
        if (ArrayUtil.isEmpty(cookies)) {
            return R.ok();
        }

        Cookie tokenCookie = null;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                tokenCookie = cookie;
                break;
            }
        }

        if (null == tokenCookie) {
            return R.ok();
        }

        String key = TOKEN_REDIS_KEY + tokenCookie.getValue();
        stringRedisTemplate.opsForValue().getAndDelete(key);

        return R.ok();
    }
}
