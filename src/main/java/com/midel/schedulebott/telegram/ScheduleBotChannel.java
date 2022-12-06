package com.midel.schedulebott.telegram;

import com.midel.schedulebott.command.CommandContainer;
import com.midel.schedulebott.config.BotConfig;
import com.midel.schedulebott.config.ChatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.midel.schedulebott.command.CommandName.*;

@Component
public class ScheduleBotChannel extends TelegramLongPollingBot {
    static final Logger logger = LoggerFactory.getLogger(ScheduleBotChannel.class);
    public static final String COMMAND_PREFIX = "/";
    private final CommandContainer commandContainer;

    public ScheduleBotChannel() {

        this.commandContainer =
                new CommandContainer(ChatConfig.ADMINS);

    }

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

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText().trim();
            String chatId = message.getChatId().toString();

            if (message.isGroupMessage() || message.isSuperGroupMessage()){
                if (text.contains("@" + BotConfig.BOT_USERNAME)){
                    text = text.replace("@" + BotConfig.BOT_USERNAME, "");
                } else {
                    return;
                }
            }

            try {
                if (text.startsWith(COMMAND_PREFIX)) {
                    String[] split = text.split(" ");
                    String commandIdentifier = split[0].toLowerCase();
                    List<String> arguments = Arrays.asList(split).subList(1, split.length);

                    commandContainer
                            .retrieveCommand(commandIdentifier, arguments, chatId)
                            .execute(update);
                } else {
                    commandContainer
                            .retrieveCommand(NO.getCommandName(), null, chatId)
                            .execute(update);
                }
            } catch (Exception e){
                commandContainer
                        .retrieveCommand(NO.getCommandName(), null, chatId)
                        .execute(update);
                logger.error("Command execution error {}", text, e);
            }
        }

   /*     if (update.hasMessage()) {
            BotController.returnAnswer(update);
        }
*/
//        if (update.hasChannelPost()){
//            new SendMessage().deleteMessage(update.getChannelPost().getChat().getId().toString(), update.getChannelPost().getMessageId());
//        }
    }
}
