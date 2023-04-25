package com.example.telegram_bot.service;

import com.example.telegram_bot.config.BotConfig;
import com.example.telegram_bot.keyboards.ConstantButton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String ERROR_OCCURRED = "Error occurred: ";
    private final BotConfig botConfig;
    private final UserService userService;
    private final BotSendMessage botSendMessage;
    private String textFromUser;

    public TelegramBot(BotConfig botConfig, UserService userService, BotSendMessage botSendMessage) {
        this.botConfig = botConfig;
        this.userService = userService;
        this.botSendMessage = botSendMessage;

        try {
            this.execute(new SetMyCommands(getListBotCommand(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            log.error("Error setting bots command list: " + ex.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            if (messageText.equals(ConstantButton.START.getButton())) {
                startCommandReceived(update);
            } else if (messageText.equals(ConstantButton.WANT_SETUP.getButton())) {
                setupCommandReceived(update, ConstantButton.WANT_SETUP.getButton());
            } else if (messageText.equals(ConstantButton.CALL_ME.getButton())) {
                helpCommandReceived(update);
            } else if (messageText.equals(ConstantButton.YES.getButton())) {

            } else if (checkUserMessage(update)) {
                textFromUser = update.getMessage().getText();
                setupCommandReceived(update, ConstantButton.YES.getButton());
            }
        }
//        else if (update.hasCallbackQuery()) {
//            String callBackData = update.getCallbackQuery().getData();
//            long messageId = update.getCallbackQuery().getMessage().getMessageId();
//            long chatId = update.getCallbackQuery().getMessage().getChatId();
//
//            if (callBackData.equals(ConstantButton.READY.getButton())) {
//                CheckMessage checkMessage = new CheckMessage();
//                String one = update.getCallbackQuery().getMessage().getText();
//                String yes = "You press YES BUTTON";
//                getEditMessageText(messageId, chatId, one);
//            } else if (callBackData.equals(ConstantButton.CANCEL.getButton())) {
//                String no = "Вы ничего не ввели";
//                getEditMessageText(messageId, chatId, no);
//            }
//        }
    }

    private boolean checkUserMessage(Update update) {
        return CheckMessage.checkUserMessage(update.getMessage().getText());
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    private List<BotCommand> getListBotCommand() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(ConstantButton.START.getButton(), "get a welcome message"));

        return listOfCommands;
    }

    private void getEditMessageText(long messageId, long chatId, String yesNo) {
        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(chatId);
        messageText.setText(yesNo);
        messageText.setMessageId(Math.toIntExact(messageId));

        try {
            execute(messageText);
        } catch (TelegramApiException ex) {
            log.error(ERROR_OCCURRED + ex.getMessage());
        }
    }

    private void startCommandReceived(Update update) {
        try {
            execute(botSendMessage.getStartMessage(update.getMessage()));
        } catch (TelegramApiException e) {
            log.error(ERROR_OCCURRED + e.getMessage());
        }
    }

    private void setupCommandReceived(Update update, String button) {
        SendMessage sendMessage = new SendMessage();
        if (button.equals(ConstantButton.WANT_SETUP.getButton())) {
            sendMessage = botSendMessage.setupDiscuss(update.getMessage());
        } else if (button.equals(ConstantButton.YES.getButton())) {
            sendMessage = botSendMessage.sendMessageWhereWriteUser(update.getMessage());
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_OCCURRED + e.getMessage());
        }
    }

    private void helpCommandReceived(Update update) {
        try {
            execute(botSendMessage.getHelpMessage(update.getMessage()));
        } catch (TelegramApiException e) {
            log.error(ERROR_OCCURRED + e.getMessage());
        }
    }

    private void registerUser(Message message) {
        userService.userRegistration(message);
    }
}
