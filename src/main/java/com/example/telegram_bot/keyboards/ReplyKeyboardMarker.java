package com.example.telegram_bot.keyboards;

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
        KeyboardButton one = new KeyboardButton();
        firstLine.add(new KeyboardButton(ConstantButton.WANT_SETUP.getButton()));
        firstLine.add(new KeyboardButton(ConstantButton.CALL_ME.getButton()));

        return getReplyKeyboardMarkup(firstLine);
    }

    public ReplyKeyboardMarkup getReadyOrCancelKeyboard(){
        KeyboardRow firstLine = new KeyboardRow();
        KeyboardButton one = new KeyboardButton();
        firstLine.add(new KeyboardButton(ConstantButton.YES.getButton()));
        firstLine.add(new KeyboardButton(ConstantButton.CANCEL.getButton()));

        return getReplyKeyboardMarkup(firstLine);
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup(KeyboardRow firstLine) {
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
