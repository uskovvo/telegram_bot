package com.example.telegram_bot.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckMessage {

    private static boolean checkResult = false;
    private static final String REGEX_PHONE = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
    private static final String REGEX_LETTER = "[A-Za-z]*";
    private static final Pattern PATTERN_PHONE = Pattern.compile(REGEX_PHONE);
    private static final Pattern PATTERN_BIO = Pattern.compile(REGEX_LETTER);

    public static boolean checkUserMessage(String userMessage) {
        userMessage = userMessage.trim();
        String number = "";
        String bio = "";
        String[] m = userMessage.split(" ");

        for (String s : m) {
            Matcher numberMatcher = PATTERN_PHONE.matcher(s);
            Matcher bioMatcher = PATTERN_BIO.matcher(s);
            if (numberMatcher.find()) {
                number = s;
            } else if (bioMatcher.find()) {
                bio = bio.concat(s).concat(" ");
            }
        }

        if (!number.isEmpty() && !bio.isEmpty()) {
            checkResult = true;
        }

        return checkResult;
    }

    public static void getBioUser(String userMessage) {
        userMessage = userMessage.replaceAll("[+\\d]", "");
        String[] bio = userMessage.split(" ");

        System.out.println(userMessage.trim());
    }
}
