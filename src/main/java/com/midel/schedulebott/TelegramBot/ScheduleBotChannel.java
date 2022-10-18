package com.midel.schedulebott.TelegramBot;

import com.midel.schedulebott.Config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ScheduleBotChannel extends TelegramLongPollingBot {
    static final Logger logger = LoggerFactory.getLogger(ScheduleBotChannel.class);
    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("{}", update);
        if (update.hasMessage()) {
            BotController.returnAnswer(update);
        }

//        if (update.hasChannelPost()){
//            new SendMessage().deleteMessage(update.getChannelPost().getChat().getId().toString(), update.getChannelPost().getMessageId());
//        }
    }
}
