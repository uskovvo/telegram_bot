package com.example.telegram_bot.service;

import com.example.telegram_bot.config.BotConfig;
import com.example.telegram_bot.model.ConstantButton;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final LocalTime MORNING = LocalTime.of(5, 59);
    private static final LocalTime AFTERNOON = LocalTime.of(9, 59);
    private static final LocalTime EVENING = LocalTime.of(16, 59);
    private static final LocalTime NIGHT = LocalTime.of(21, 59);
    private static final String ERROR_OCCURRED = "Error occurred: ";
    private final BotConfig botConfig;
    private final UserService userService;

    private static final String HI_MESSAGE = "Привет! Я ваш помощник. Для начала нажмите /start";
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

            if (callBackData.equals(ConstantButton.YES_BUTTON.name())) {
                String yes = "You press YES BUTTON";
                getEditMessageText(messageId, chatId, yes);
            } else if (callBackData.equals(ConstantButton.NO_BUTTON.name())) {
                String no = "You press NO BUTTON";
                getEditMessageText(messageId, chatId, no);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    private List<BotCommand> getListBotCommand() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
//        listOfCommands.add(new BotCommand("/register", "set your settings"));
//        listOfCommands.add(new BotCommand("/mydata", "show your data stored"));
//        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
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
        LocalTime timeOfDay = LocalTime.now();
        String answer = "";
        if(timeOfDay.isAfter(NIGHT)) {
            answer = EmojiParser.parseToUnicode("Доброй ночи, " + firstName + ", я вам помогу создать заявку :fire:");
        } else if(timeOfDay.isAfter(EVENING)) {
            answer = EmojiParser.parseToUnicode("Добрый вечер, " + firstName + ", я вам помогу создать заявку :fire:");
        } else if(timeOfDay.isAfter(MORNING)) {
            answer = EmojiParser.parseToUnicode("Доброе утро, " + firstName + ", я вам помогу создать заявку :fire:");
        } else if(timeOfDay.isAfter(AFTERNOON)){
            answer = EmojiParser.parseToUnicode("Добрый день, " + firstName + ", я вам помогу создать заявку :fire:");
        }

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
        buttonYes.setCallbackData(ConstantButton.YES_BUTTON.name());

        var buttonNo = new InlineKeyboardButton();
        buttonNo.setText("No");
        buttonNo.setCallbackData(ConstantButton.NO_BUTTON.name());

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
