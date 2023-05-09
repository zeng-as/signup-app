package com.as.signup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@ServletComponentScan
@EnableOpenApi
@MapperScan("com.as.signup.mapper")
public class FiveSignupApplication {
    public static void main(String[] args) {
        SpringApplication.run(FiveSignupApplication.class, args);
    }
}
