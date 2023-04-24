package com.example.telegram_bot.service;

import com.example.telegram_bot.model.orderPackage.OrderEntity;
import com.example.telegram_bot.model.orderPackage.OrderRepository;
import com.example.telegram_bot.model.userPackage.UserEntity;
import com.example.telegram_bot.model.userPackage.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.ArrayList;

@Slf4j
@Component
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public UserService (UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public void userRegistration(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();

            UserEntity userEntity = new UserEntity();
            userEntity.setChatId(chatId);
            userEntity.setFirstName(chat.getFirstName());
            userEntity.setLastName(chat.getLastName());
            userEntity.setUserName(chat.getUserName());
            userEntity.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userEntity.setOrders(new ArrayList<>());
            userRepository.save(userEntity);
            OrderEntity o = new OrderEntity();
            o.setDateOrderWasAdded(new Timestamp(System.currentTimeMillis()));
            o.setUserEntity(userEntity);
            orderRepository.save(o);
            userEntity.getOrders().add(o);
            log.info("User saved: " + userEntity);
        }
    }
}
