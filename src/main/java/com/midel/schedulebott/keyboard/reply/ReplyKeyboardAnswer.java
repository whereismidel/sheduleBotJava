package com.midel.schedulebott.keyboard.reply;

public enum ReplyKeyboardAnswer {

    HELP("Доступні команди"),
    LEADER_MENU("Меню старости"),
    UNKNOWN("");

    private final String userAnswer;

    ReplyKeyboardAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

}