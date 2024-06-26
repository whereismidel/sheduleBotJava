package com.midel.telegram;

import com.midel.command.CommandContainer;
import com.midel.command.CommandName;
import com.midel.config.BotConfig;
import com.midel.config.ChatConfig;
import com.midel.keyboard.inline.InlineKeyboardContainer;
import com.midel.keyboard.reply.ReplyKeyboardContainer;
import com.midel.reply_message.ReplyMessageContainer;
import com.midel.reply_message.ReplyMessageKey;
import com.midel.student.StudentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@Component
public class ScheduleBotChannel extends TelegramLongPollingBot {
    static final Logger logger = LoggerFactory.getLogger(ScheduleBotChannel.class);
    public static final String COMMAND_PREFIX = "/";
    public static final String BUTTON_PREFIX = "\uD83D\uDD25";
    private final CommandContainer commandContainer;
    private final ReplyKeyboardContainer replyKeyboardContainer;
    private final InlineKeyboardContainer inlineKeyboardContainer;
    private final ReplyMessageContainer replyMessageContainer;

    public ScheduleBotChannel() {

        this.commandContainer = new CommandContainer(ChatConfig.ADMINS);

        this.replyKeyboardContainer = new ReplyKeyboardContainer();

        this.inlineKeyboardContainer = new InlineKeyboardContainer();

        this.replyMessageContainer = new ReplyMessageContainer();

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
        logger.trace("{}", update);
  //      System.out.println(update);

        if (update.hasCallbackQuery()){
            inlineKeyboardContainer
                    .retrieveCommand(update.getCallbackQuery().getData())
                    .execute(update);
        } else if (update.hasMyChatMember()){
            replyMessageContainer.retrieveCommand(ReplyMessageKey.CREATE_CHANNEL.getKeyMessage()).execute(update);
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText().trim();
            String chatId = message.getChatId().toString();

            if (StudentController.isFlood(chatId)){
                commandContainer
                        .retrieveCommand(CommandName.FLOOD.getCommandName(), null, update)
                        .execute(update);
                return;
            }

            if (message.isGroupMessage() || message.isSuperGroupMessage()){
                if (text.contains("@" + BotConfig.BOT_USERNAME)){
                    text = text.replace("@" + BotConfig.BOT_USERNAME, "");
                } else {
                    commandContainer
                            .retrieveCommand(CommandName.IGNORE_COMMAND.getCommandName(), null, update)
                            .execute(update);

                    return;
                }
            }

            try {
                if (text.startsWith(COMMAND_PREFIX)) {
                    String[] split = text.split(" ");
                    String commandIdentifier = split[0].toLowerCase();
                    List<String> arguments = Arrays.asList(split).subList(1, split.length);

                    commandContainer
                            .retrieveCommand(commandIdentifier, arguments, update)
                            .execute(update);
                } else if (text.startsWith(BUTTON_PREFIX)) {
                    replyKeyboardContainer
                            .retrieveCommand(text.replace(BUTTON_PREFIX,""))
                            .execute(update);
                } else if (message.getReplyToMessage() != null) {
                    String replyKey = message.getReplyToMessage().getText().split("\n")[0];
                    update.getMessage().setText(text);
                    update.setMessage(update.getMessage());
                    replyMessageContainer
                            .retrieveCommand(replyKey)
                            .execute(update);
                } else {
                    commandContainer
                            .retrieveCommand(CommandName.NO.getCommandName(), null, update)
                            .execute(update);
                }
            } catch (Exception e){
                commandContainer
                        .retrieveCommand(CommandName.NO.getCommandName(), null, update)
                        .execute(update);
                commandContainer.retrieveCommand(CommandName.START.getCommandName(), null, update).execute(update);
                logger.error("Command execution error {}", text, e);
            }
        }
    }
}