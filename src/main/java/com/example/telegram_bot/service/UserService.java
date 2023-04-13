package com.example.telegram_bot.service;

import com.example.telegram_bot.model.UserEntity;
import com.example.telegram_bot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

@Slf4j
@Component
public class UserService {

    private final UserRepository userRepository;

    public UserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void userRegistration(Message message){
        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();

            UserEntity userEntity = new UserEntity();
            userEntity.setChatId(chatId);
            userEntity.setFirstName(chat.getFirstName());
            userEntity.setLastName(chat.getLastName());
            userEntity.setUserName(chat.getUserName());
            userEntity.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(userEntity);
            log.info("User saved: " + userEntity);
        }
    }
}
