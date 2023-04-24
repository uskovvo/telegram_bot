package com.example.telegram_bot.model.userPackage;

import com.example.telegram_bot.model.userPackage.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
