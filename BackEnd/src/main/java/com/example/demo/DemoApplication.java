package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo")
@EntityScan(basePackages = "com.example.demo")
public class DemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);

        System.out.println(">>> MAIN RUNNING FROM: " +
                DemoApplication.class.getProtectionDomain().getCodeSource().getLocation());

        System.out.println(">>> SecurityFilterChain beans: " +
                ctx.getBeansOfType(SecurityFilterChain.class).keySet());
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }
}