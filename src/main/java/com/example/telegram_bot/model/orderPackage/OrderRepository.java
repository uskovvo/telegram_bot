package com.example.telegram_bot.model.orderPackage;

import com.example.telegram_bot.model.orderPackage.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
