package com.zsh.teach_oa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TeachOaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeachOaApplication.class, args);
    }

}
