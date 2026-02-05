package com.example.demo.scheduler;

import com.example.demo.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserCountScheduler {

    private final UserService userService;

    public UserCountScheduler(UserService userService) {
        this.userService = userService;
    }
    @Scheduled(fixedRate = 60000)
    public void reportUserCount() {
        int total = userService.getAll().size();
        System.out.println("Hiện tại có " + total + " user trong hệ thống.");
    }
}
