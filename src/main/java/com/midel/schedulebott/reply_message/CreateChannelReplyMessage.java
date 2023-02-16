package com.midel.schedulebott.reply_message;

import com.midel.schedulebott.config.ChatConfig;
import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.group.GroupController;
import com.midel.schedulebott.group.GroupRepo;
import com.midel.schedulebott.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.schedulebott.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Request from {@link LeaderMenuReplyKeyboardCommand}
 */
public class CreateChannelReplyMessage extends ReplyMessage {

    private static final Logger logger = LoggerFactory.getLogger(CreateChannelReplyMessage.class);
    public static final String SUCCESSFUL_MESSAGE = "Канал успішно додано.\n"
                                    +"Якщо адмін. права не виставлені правильно - в майбутньому можуть бути проблеми з розкладом.";

    public static final String FAILED_UPDATE_CHANNEL = "Канал не вдалось додати, для усунення проблеми спробуй:\n"
            +"1) Видалити бота з канала і додати його ще раз з відповідмини правами\n"
            +"2) Пройти весь процес реєстрації заново <code>/restart</code>\n"
            +"3) Нвписати " + ChatConfig.creatorUsername;
    public CreateChannelReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        ChatMemberUpdated memberUpdated = update.getMyChatMember();
        String userId = memberUpdated.getFrom().getId().toString();

        Group group = GroupController.getGroupByLeader(userId);

        if (group != null && group.getChannelId() == null) {
            try {

                group.setChannelId(memberUpdated.getChat().getId().toString());

                int message = sendMessage.sendHTMLMessage(group.getChannelId(), "Перевірка можливості надсилання повідомлень.");

                if (message == -1 || !sendMessage.deleteMessage(group.getChannelId(),message)){
                    throw new Exception();
                }
                new SendMessage().changeDescription(group.getChannelId(), "Тут ти можеш отримувати повідомлення, які пов'язані з розкладом. Будь-які пропозиції - " + ChatConfig.creatorUsername);

                if (!GroupRepo.exportGroupList()) {
                    throw new Exception();
                }
                sendMessage.sendHTMLMessage(userId, SUCCESSFUL_MESSAGE);
                logger.info("Added a channel to the group. groupName = {}, channelId = {}", group.getGroupName(), group.getChannelId());

                new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update, userId);
            } catch (Exception e){
                sendMessage.sendHTMLMessage(userId, FAILED_UPDATE_CHANNEL);
                logger.warn("Failed to add channel to group. {}", group, e);
                group.setChannelId(null);
            }
        }
    }
}