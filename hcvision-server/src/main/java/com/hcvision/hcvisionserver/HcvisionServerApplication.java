package com.hcvision.hcvisionserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class HcvisionServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HcvisionServerApplication.class, args);
    }
}
