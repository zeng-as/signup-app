package com.as.signup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@MapperScan("com.as.signup.mapper")
public class FiveSignupApplication {
    public static void main(String[] args) {
        SpringApplication.run(FiveSignupApplication.class, args);
    }
}
