package com.midel.schedulebott;

import com.midel.schedulebott.Group.Group;
import com.midel.schedulebott.Group.GroupController;
import com.midel.schedulebott.Schedule.ScheduledTask;
import com.midel.schedulebott.TelegramBot.ScheduleBotChannel;
import com.midel.schedulebott.TelegramBot.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class BotInitialization {
    private static final Logger logger = LoggerFactory.getLogger(BotInitialization.class);

    public static void main(String[] args) throws InterruptedException {
        while(true) {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new ScheduleBotChannel());

                SpringApplication.run(BotInitialization.class, args);

                logger.info("Successful connection to Telegram Bot.");

                new ScheduledTask().initAndUpdateGroupListFromSheet();

                for(Group group : GroupController.groups){
                    new SendMessage().changeDescription(group.getChannelId(), "Тут ти можеш отримувати повідомлення, які пов'язані з розкладом. Будь-які пропозиції - @Midell");
                }

                return;
            } catch (TelegramApiException e) {
                logger.error("Telegram connection failed. Reconnection...", e);
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }
}
