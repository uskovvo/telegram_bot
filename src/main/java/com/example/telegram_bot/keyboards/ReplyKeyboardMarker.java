package com.example.telegram_bot.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplyKeyboardMarker {

    public ReplyKeyboardMarkup getMainMenuKeyboard(){
        KeyboardRow firstLine = new KeyboardRow();
        firstLine.add(new KeyboardButton("Хочу монтаж"));
        firstLine.add(new KeyboardButton("Позвони мне"));

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(firstLine);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
}
