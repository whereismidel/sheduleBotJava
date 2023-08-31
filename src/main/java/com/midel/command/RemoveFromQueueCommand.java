package com.midel.command;

import com.midel.command.annotation.AdminCommand;
import com.midel.config.BotConfig;
import com.midel.config.ChatConfig;
import com.midel.keyboard.inline.QueueLeaveInlineHandler;
import com.midel.queue.Queue;
import com.midel.queue.QueueController;
import com.midel.telegram.SendMessage;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@AdminCommand
public class RemoveFromQueueCommand extends Command {

    public final static String INFO_MESSAGE = "Reply to queue <code>/removefromqueue num_in_queue@" + BotConfig.BOT_USERNAME + "</code> - видалити з черги користувача.";
    public RemoveFromQueueCommand(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        User from = update.getMessage().getFrom();

        try {
            if (ChatConfig.ADMINS.contains(from.getId().toString())) {
                if (arguments != null) {
                    if (arguments.size() == 1 && update.getMessage().getReplyToMessage() != null) {
                        update.setCallbackQuery(new CallbackQuery());
                        update.getCallbackQuery().setMessage(new Message());
                        update.getCallbackQuery().getMessage().setChat(update.getMessage().getChat());
                        update.getCallbackQuery().getMessage().setMessageId(update.getMessage().getReplyToMessage().getMessageId());
                        update.getCallbackQuery().getMessage().setReplyMarkup(update.getMessage().getReplyToMessage().getReplyMarkup());

                        Queue queue = QueueController.getQueueById(update.getCallbackQuery().getMessage().getChatId() +
                                "/" + update.getMessage().getReplyToMessage().getMessageId());

                        if (queue != null && Integer.parseInt(arguments.get(0)) <= queue.getUserQueue().size()) {
                            Pair<String, String> user = queue.getUserQueue().get(Integer.parseInt(arguments.get(0)));

                            update.getCallbackQuery().setFrom(new User());
                            update.getCallbackQuery().getFrom().setId(Long.parseLong(user.getValue1()));
                            update.getCallbackQuery().getFrom().setFirstName(user.getValue0());

                            new QueueLeaveInlineHandler(sendMessage).execute(update);
                        }
                    } else {
                        sendMessage.sendHTMLMessage(from.getId().toString(), INFO_MESSAGE);
                    }
                }
            }
        } catch (Exception e){
            sendMessage.sendHTMLMessage(from.getId().toString(), INFO_MESSAGE);
        }
    }
}
