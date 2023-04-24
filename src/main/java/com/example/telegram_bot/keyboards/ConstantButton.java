package com.example.telegram_bot.keyboards;

public enum ConstantButton {
    START("/start"),
    WANT_SETUP("Хочу монтаж"),
    CALL_ME("Позвони мне");

    private final String button;

    ConstantButton(String button) {
        this.button = button;
    }

    public String getButton() {
        return button;
    }
}
