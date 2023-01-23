package com.example.telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class TelegramBotApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TelegramBotApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    @RequestMapping(value = "/")
    public String hello() {
        return "Telegram Bot запущен";
    }

}
