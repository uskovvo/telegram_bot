package com.example.telegram_bot.service;

import com.example.telegram_bot.config.BotConfig;
import com.example.telegram_bot.keyboards.ConstantButton;
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
    private final BotConfig botConfig;
    private final UserService userService;
    private final BotSendMessage botSendMessage;
    private static final String HELP_TEXT = "Чтобы начать общаться со мной наберите /start";

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

            if(messageText.equals(ConstantButton.START.getButton())){
                startCommandReceived(update);
            } else if (messageText.equals(ConstantButton.WANT_SETUP.getButton())) {
                /* TODO: реализуй это!!! */
            } else if (messageText.equals(ConstantButton.CALL_ME.getButton())) {
                helpCommandReceived(update);
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(ConstantButton.WANT_SETUP.getButton())) {
                String yes = "You press YES BUTTON";
                getEditMessageText(messageId, chatId, yes);
            } else if (callBackData.equals(ConstantButton.CALL_ME.getButton())) {
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

    private void helpCommandReceived(Update update) {
        try {
            execute(botSendMessage.getHelpMessage(update.getMessage()));
        } catch (TelegramApiException e) {
            log.error(ERROR_OCCURRED + e.getMessage());
        }
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

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
        buttonYes.setCallbackData(ConstantButton.WANT_SETUP.getButton());

        var buttonNo = new InlineKeyboardButton();
        buttonNo.setText("No");
        buttonNo.setCallbackData(ConstantButton.CALL_ME.getButton());

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
