package com.achievementtracker.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableFeignClients(basePackages = {"com.achievementtracker.proxy"})
@EnableScheduling
public class Config {

}
