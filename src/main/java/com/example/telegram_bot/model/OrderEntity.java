package com.example.telegram_bot.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "orderData")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp dateOrderWasAdded;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDateOrderWasAdded() {
        return dateOrderWasAdded;
    }

    public void setDateOrderWasAdded(Timestamp dateOrderWasAdded) {
        this.dateOrderWasAdded = dateOrderWasAdded;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}