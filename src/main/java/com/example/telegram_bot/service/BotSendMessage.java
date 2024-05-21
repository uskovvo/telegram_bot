package com.example.telegram_bot.service;

import com.example.telegram_bot.keyboards.ReplyKeyboardMarker;
import com.github.sinboun.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalTime;

@Component
@Slf4j
public class BotSendMessage {
    private static final LocalTime MORNING = LocalTime.of(5, 59);
    private static final LocalTime AFTERNOON = LocalTime.of(9, 59);
    private static final LocalTime EVENING = LocalTime.of(16, 59);
    private static final LocalTime NIGHT = LocalTime.of(21, 59);

    private final ReplyKeyboardMarker replyKeyboardMarker;

    public BotSendMessage(ReplyKeyboardMarker replyKeyboardMarker) {
        this.replyKeyboardMarker = replyKeyboardMarker;
    }

    public SendMessage getStartMessage(Message message) {
        LocalTime timeOfDay = LocalTime.now();
        Long chatId = message.getChatId();
        String nickName = message.getChat().getUserName();
        String answer = "";
        if (timeOfDay.isAfter(NIGHT)) {
            answer = EmojiParser.parseToUnicode("Доброй ночи, " + nickName + ", я вам помогу создать заявку :fire:");
        } else if (timeOfDay.isAfter(EVENING)) {
            answer = EmojiParser.parseToUnicode("Добрый вечер, " + nickName + ", я вам помогу создать заявку :fire:");
        } else if (timeOfDay.isAfter(MORNING)) {
            answer = EmojiParser.parseToUnicode("Доброе утро, " + nickName + ", я вам помогу создать заявку :fire:");
        } else if (timeOfDay.isAfter(AFTERNOON)) {
            answer = EmojiParser.parseToUnicode("Добрый день, " + nickName + ", я вам помогу создать заявку :fire:");
        }

        log.info("Replied to User: " + nickName);
        SendMessage startMessage = new SendMessage();
        startMessage.setChatId(chatId);
        startMessage.setText(answer);
        startMessage.enableMarkdown(true);
        startMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());

        return startMessage;
    }

    public SendMessage getAllInfoAboutUser(Message message){
        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(message.getChatId());
        infoMessage.setText("Хай ёпта");

        return infoMessage;
    }

    public SendMessage setupDiscuss(Message message) {
        SendMessage discuss = new SendMessage();
        discuss.setChatId(message.getChatId());
        discuss.setText("Укажите ваши ФИО и номер телефона в формате: " +
                "\n\n\t\t\t\t" +
                "Иванов Иван Иванович +7 777 777 77 77");
//        discuss.setReplyMarkup(replyKeyboardMarker.getReadyOrCancelKeyboard());

        return discuss;
    }

    public SendMessage sendMessageWhereWriteUser(Message message){
        SendMessage userMessage = new SendMessage();
        userMessage.setChatId(message.getChatId());
        userMessage.setText("Вы указали следующие данные: " +
                "\n\n\t\t\t\t" +
                message.getText() +
                "\n\n" +
                "Все верно?");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        userMessage.setReplyMarkup(replyKeyboardMarker.getReadyOrCancelKeyboard());

        return userMessage;
    }

    public SendMessage getHelpMessage(Message message){
        SendMessage helpMessage = new SendMessage();
        helpMessage.setChatId(message.getChatId());
        helpMessage.setText("Помоги себе сам");

        return helpMessage;
    }
}
