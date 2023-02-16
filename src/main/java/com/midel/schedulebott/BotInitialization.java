package com.midel.schedulebott;

import com.midel.schedulebott.group.GroupRepo;
import com.midel.schedulebott.student.StudentRepo;
import com.midel.schedulebott.telegram.ScheduleBotChannel;
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
    public static final ScheduleBotChannel scheduleBot = new ScheduleBotChannel();

    public static boolean loadAndUpdateAllTables(){
        if (!GroupRepo.importGroupList()){
            return false;
        }

        if (!GroupRepo.importAllScheduleTables()){
            return false;
        }

        if (!StudentRepo.importStudentList()){
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        while(true) {
            try {
                SpringApplication.run(BotInitialization.class, args);

                while (true){
                    if (loadAndUpdateAllTables()){
                        logger.info("Successful initiation of data from tables.");
                        break;
                    } else {
                        try{
                            logger.error("Failed to update data from tables Schedule and Subject. Retrying..");
                            TimeUnit.SECONDS.sleep(15);
                        } catch (InterruptedException e){
                            logger.error("FATAL: Wait error while retrying table import request.", e);
                            return;
                        }
                    }
                }

                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(scheduleBot);

                logger.info("Successful connection to Telegram Bot.");
                return;
            } catch (TelegramApiException e) {
                logger.error("Telegram connection failed. Reconnection...", e);
                TimeUnit.SECONDS.sleep(30);
            }
        }
    }
}
