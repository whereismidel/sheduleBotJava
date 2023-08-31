package com.midel.command;

import com.midel.command.annotation.AdminCommand;
import com.midel.config.BotConfig;
import com.midel.config.ChatConfig;
import com.midel.keyboard.inline.QueueJoinInlineHandler;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@AdminCommand
public class AddToQueueCommand extends Command {

    public final static String INFO_MESSAGE = "Reply to queue <code>/addToQueue name id position@" + BotConfig.BOT_USERNAME + "</code> - додати до черги користувача.";
    public AddToQueueCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        User from = update.getMessage().getFrom();

        try {
            if (ChatConfig.ADMINS.contains(from.getId().toString())) {
                if (arguments != null) {
                    if (arguments.size() == 3 && update.getMessage().getReplyToMessage() != null) {
                        update.setCallbackQuery(new CallbackQuery());
                        update.getCallbackQuery().setMessage(new Message());
                        update.getCallbackQuery().getMessage().setChat(update.getMessage().getChat());
                        update.getCallbackQuery().getMessage().setMessageId(update.getMessage().getReplyToMessage().getMessageId());
                        update.getCallbackQuery().setFrom(new User());
                        update.getCallbackQuery().getFrom().setId(Long.parseLong(arguments.get(1)));
                        update.getCallbackQuery().getFrom().setFirstName(arguments.get(0).replace("/"," "));
                        update.getCallbackQuery().getMessage().setReplyMarkup(update.getMessage().getReplyToMessage().getReplyMarkup());

                        update.setMessage(new Message());
                        update.getMessage().setText(arguments.get(2));

                        new QueueJoinInlineHandler(sendMessage).execute(update);
                    } else {
                        sendMessage.sendHTMLMessage(from.getId().toString(), INFO_MESSAGE);
                    }
                }
            }
        } catch (Exception e){
//            e.printStackTrace();
            sendMessage.sendHTMLMessage(from.getId().toString(), INFO_MESSAGE);
        }
    }


}
