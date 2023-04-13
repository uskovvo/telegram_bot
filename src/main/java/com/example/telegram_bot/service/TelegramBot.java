package com.example.telegram_bot.service;

import com.example.telegram_bot.config.BotConfig;
import com.github.sinboun.EmojiParser;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String ERROR_OCCURRED = "Error occurred: ";
    private static final String YES = "YES_BUTTON";
    private static final String NO = "NO_BUTTON";
    private final BotConfig botConfig;
    private final UserService userService;
    private static final String HELP_TEXT = "I can't help you";

    public TelegramBot(BotConfig botConfig, UserService userService) {
        this.botConfig = botConfig;
        this.userService = userService;

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
            Long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getChat().getFirstName();
            Message message = update.getMessage();

            switch (messageText) {
                case "/start":
                    registerUser(message);
                    startCommandReceived(chatId, firstName);
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/register":
                    register(chatId);
                    break;
                default:
                    String defaultMessage = EmojiParser.parseToUnicode("Sorry :disappointed:");
                    sendMessage(chatId, defaultMessage);
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(YES)) {
                String yes = "You press YES BUTTON";
                getEditMessageText(messageId, chatId, yes);
            } else if (callBackData.equals(NO)) {
                String no = "You press NO BUTTON";
                getEditMessageText(messageId, chatId, no);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    private List<BotCommand> getListBotCommand(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
//        listOfCommands.add(new BotCommand("/register", "set your settings"));
//        listOfCommands.add(new BotCommand("/mydata", "show your data stored"));
//        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
//        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
//        listOfCommands.add(new BotCommand("/settings", "set your preferences"));

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

    private void startCommandReceived(Long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode("Hi, " + firstName + ", nice to me you" + " :fire:");
        log.info("Replied to User: " + firstName);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        /* Начало создания экранной клавиатуры в боте **/

//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        row.add("weather");
//        row.add("get random joke");
//
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//
//        row.add("check my data");
//        row.add("delete my data");
//
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//
//        message.setReplyMarkup(keyboardMarkup);

        /* конец создания клавиатуры **/

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error(ERROR_OCCURRED + ex.getMessage());
        }
    }

    private void registerUser(Message message) {
        userService.userRegistration(message);
    }

    private void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var buttonYes = new InlineKeyboardButton();
        buttonYes.setText("Yes");
        buttonYes.setCallbackData(YES);

        var buttonNo = new InlineKeyboardButton();
        buttonNo.setText("No");
        buttonNo.setCallbackData(NO);

        rowInLine.add(buttonYes);
        rowInLine.add(buttonNo);

        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error(ERROR_OCCURRED + ex.getMessage());
        }
    }
}
