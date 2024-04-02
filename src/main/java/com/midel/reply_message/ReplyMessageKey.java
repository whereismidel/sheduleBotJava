package com.midel.reply_message;

/**
 * The key of the reply message is the first line before the line break
 */
public enum ReplyMessageKey {

    SET_GROUP("Вкажіть групу"),
    CREATE_CHANNEL("Створено канал"),
    CREATE_AND_SHARE_SHEET("Залишилась пошта"),
    UNKNOWN_REPLY(""),
    DELETE_STUDENT("Видалення користувача"),
    DELETE_LEADER("Видалення аккаунту"),
    CREATE_STUDENT("Група не вказана");

    private final String keyMessage;

    ReplyMessageKey(String keyMessage) {
        this.keyMessage = keyMessage;
    }

    public String getKeyMessage() {
        return keyMessage;
    }

}
