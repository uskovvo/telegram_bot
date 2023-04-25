package com.example.telegram_bot.keyboards;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class InlineKeyboardMarker {

    public InlineKeyboardMarkup getInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listInlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> listButtons = new ArrayList<>();

        InlineKeyboardButton readyButton = new InlineKeyboardButton();
        readyButton.setText(ConstantButton.YES.getButton());
        readyButton.setCallbackData(ConstantButton.YES.getButton());

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText(ConstantButton.CANCEL.getButton());
        cancelButton.setCallbackData(ConstantButton.CANCEL.getButton());

        listButtons.add(readyButton);
        listButtons.add(cancelButton);
        listInlineButtons.add(listButtons);
        inlineKeyboardMarkup.setKeyboard(listInlineButtons);

        return inlineKeyboardMarkup;
    }
}
