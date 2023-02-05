package com.midel.schedulebott.telegram;

import com.midel.schedulebott.command.CommandContainer;
import com.midel.schedulebott.config.BotConfig;
import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.keyboard.inline.InlineKeyboardContainer;
import com.midel.schedulebott.keyboard.reply.ReplyKeyboardContainer;
import com.midel.schedulebott.reply_message.ReplyMessageContainer;
import com.midel.schedulebott.student.StudentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.midel.schedulebott.command.CommandName.FLOOD;
import static com.midel.schedulebott.command.CommandName.NO;

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

        if (update.hasCallbackQuery()){
            inlineKeyboardContainer
                    .retrieveCommand(update.getCallbackQuery().getData())
                    .execute(update);
        } else if (update.hasMyChatMember()){
            ChatMemberUpdated member = update.getMyChatMember();
            if (member.getChat().getType().equals("channel") &&
                    member.getNewChatMember().getStatus().equals("administrator") &&
                    member.getNewChatMember().getUser().getUserName().equals(BotConfig.BOT_USERNAME)){

                replyMessageContainer.retrieveCommand("Створено канал").execute(update);
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText().trim();
            String chatId = message.getChatId().toString();

            if (StudentController.isFlood(chatId)){
                commandContainer
                        .retrieveCommand(FLOOD.getCommandName(), null, chatId)
                        .execute(update);
                return;
            }

            if (message.isGroupMessage() || message.isSuperGroupMessage()){
                if (text.contains("@" + BotConfig.BOT_USERNAME)){
                    text = text.replace("@" + BotConfig.BOT_USERNAME, "");
                } else {
                    commandContainer
                            .retrieveCommand(NO.getCommandName(), null, chatId)
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
                            .retrieveCommand(commandIdentifier, arguments, chatId)
                            .execute(update);
                } else if (text.startsWith(BUTTON_PREFIX)) {
                    replyKeyboardContainer
                            .retrieveCommand(text.replace(BUTTON_PREFIX,""))
                            .execute(update);
                } else if (message.getReplyToMessage() != null) {
                    String replyKey = message.getReplyToMessage().getText().split("\n")[0];

                    replyMessageContainer
                            .retrieveCommand(replyKey)
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
    }
}