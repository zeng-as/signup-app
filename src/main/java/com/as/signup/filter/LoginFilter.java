package com.as.signup.filter;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpStatus;
import com.as.signup.common.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.as.signup.common.CommonConstants.TOKEN_REDIS_KEY;

@WebFilter(filterName = "loginFilter", urlPatterns = "/user/*")
@Order()
@Slf4j
public class LoginFilter implements Filter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Cookie[] cookies = request.getCookies();

        // 校验token
        if (ArrayUtil.isEmpty(cookies)) {
            this.unauthorized(response);
            return;
        }

        Cookie tokenCookie = null;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                tokenCookie = cookie;
                break;
            }
        }

        if (null == tokenCookie) {
            this.unauthorized(response);
            return;
        }

        String token = tokenCookie.getValue();
        // 校验token
        String key = TOKEN_REDIS_KEY + token;
        String mobile = stringRedisTemplate.opsForValue().getAndExpire(key, 60, TimeUnit.MINUTES);
        if (null == mobile) {
            this.unauthorized(response);
            return;
        } else {
            UserThreadLocal.currentUser.set(mobile);
        }

        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
    }
}
