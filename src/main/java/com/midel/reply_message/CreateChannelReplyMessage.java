package com.midel.reply_message;

import com.midel.command.MenuCommand;
import com.midel.config.BotConfig;
import com.midel.config.ChatConfig;
import com.midel.group.Group;
import com.midel.group.GroupController;
import com.midel.group.GroupRepo;
import com.midel.keyboard.reply.LeaderMenuReplyKeyboardCommand;
import com.midel.telegram.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;

import java.util.Collections;
import java.util.List;

/**
 * Request from {@link LeaderMenuReplyKeyboardCommand}
 */
public class CreateChannelReplyMessage extends ReplyMessage {

    private static final Logger logger = LoggerFactory.getLogger(CreateChannelReplyMessage.class);
    public static final String SUCCESSFUL_MESSAGE = "Канал/групу успішно додано.\n"
                                    +"Якщо адмін. права не виставлені правильно - в майбутньому можуть бути проблеми з розкладом.";

    public static final String FAILED_UPDATE_CHANNEL = "Канал/групу не вдалось додати, для усунення проблеми спробуй:\n"
            +"1) Видалити бота з каналу/групи і додати його ще раз з відповідмини правами\n"
            +"2) Пройти весь процес реєстрації заново <code>/restart</code>\n"
            +"3) Написати " + ChatConfig.creatorUsername;

    public static final String INCORRECT_PERMISSION_MESSAGE = "Прив'язаний чат/канал не надав/забрав необхідні права доступу для мене. Можливі проблеми з коректною роботою, зміни права на необхідні.";
    public static final String KICKED_MESSAGE = "Прив'язаний чат/канал вилучив мене з учасників. Надсилання розкладу буде призупинено.";
    public CreateChannelReplyMessage(SendMessage sendMessage) {
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {

        ChatMemberUpdated memberUpdated = update.getMyChatMember();
        String userId = memberUpdated.getFrom().getId().toString();
        Group group = GroupController.getGroupByLeader(userId);

        if (group != null && (group.getChannelId() == null || group.getChannelId().equals(memberUpdated.getChat().getId().toString()))) {
            switch (handleMemberAction(memberUpdated)) {
                case ADMINISTRATOR: {
                    break; // continue main logic
                }
                case WARNING: {
                    sendMessage.sendHTMLMessage(userId, INCORRECT_PERMISSION_MESSAGE);
                    return;
                }
                case LEFT: {
                    if (group.getChannelId() != null) {
                        try {
                            sendMessage.sendHTMLMessage(userId, KICKED_MESSAGE);

                            group.setChannelId(null);
                            group.getSettings().setState(false);

                            if (!GroupRepo.exportGroupList()) {
                                throw new Exception();
                            }

                            logger.info("The bot was kicked. Remove channel/groupchat from the group. groupName = {}", group.getGroupName());

                            new MenuCommand(sendMessage).execute(update, userId);
                        } catch (Exception e){
                            logger.warn("Failed to remove channel/groupchat from the group. {}", group, e);
                        }
                    }
                    return;
                }
                case IGNORE: {
                    return;
                }
            }

            if (group.getChannelId() == null) {
                try {

                    group.setChannelId(memberUpdated.getChat().getId().toString());

                    if (!GroupRepo.exportGroupList()) {
                        throw new Exception();
                    }
                    sendMessage.sendHTMLMessage(userId, SUCCESSFUL_MESSAGE);
                    logger.info("Added a channel/groupchat to the group. groupName = {}, chatId = {}", group.getGroupName(), group.getChannelId());

                    new LeaderMenuReplyKeyboardCommand(sendMessage).execute(update, userId);
                } catch (Exception e){
                    sendMessage.sendHTMLMessage(userId, FAILED_UPDATE_CHANNEL);
                    logger.warn("Failed to add channel/groupchat to group. {}", group, e);
                    group.setChannelId(null);
                }
            }
        }
    }

    // return administrator if all required permissions have been granted/changed
    // return left if bot was kicked/banned from groupchat/channel
    // return ignore for other actions with the bot
    // return warning if permission have been changed
    private static ChatMemberAction handleMemberAction(ChatMemberUpdated chatMemberUpdated) {
//        List<String> allowedChatTypes = Arrays.asList("channel", "supergroup", "group");
        List<String> allowedChatTypes = Collections.singletonList("channel");
        String chatType = chatMemberUpdated.getChat().getType();
        if (!allowedChatTypes.contains(chatType)) {
            return ChatMemberAction.IGNORE;
        }

        if (!chatMemberUpdated.getNewChatMember().getUser().getUserName().equals(BotConfig.BOT_USERNAME)) {
            return ChatMemberAction.IGNORE;
        }

        String status = chatMemberUpdated.getNewChatMember().getStatus();
        if (status.equals("administrator")){
            ChatMemberAdministrator chatAdministrator = (ChatMemberAdministrator) chatMemberUpdated.getNewChatMember();

            if (checkAdminRights(chatAdministrator, chatType)) {
                if (chatMemberUpdated.getOldChatMember().getStatus().equals("administrator")) {
                    ChatMemberAdministrator oldChatMemberState = (ChatMemberAdministrator) chatMemberUpdated.getOldChatMember();
                    if (checkAdminRights(oldChatMemberState, chatType)) {
                        return ChatMemberAction.IGNORE;
                    }
                }
                return ChatMemberAction.ADMINISTRATOR;
            } else {
                return ChatMemberAction.WARNING;
            }

        } else if (status.equals("kicked") || status.equals("banned") || status.equals("restricted")){
            return ChatMemberAction.LEFT;
        } else {
            return ChatMemberAction.IGNORE;
        }
    }

    private static boolean checkAdminRights(ChatMemberAdministrator chatAdministrator, String groupType) {
        if (groupType.equals("group") || groupType.equals("supergroup")) {
            return chatAdministrator.getCanDeleteMessages() &&
                    chatAdministrator.getCanPinMessages();
        } else if (groupType.equals("channel")) {
            return chatAdministrator.getCanPostMessages() &&
                    chatAdministrator.getCanEditMessages() &&
                    chatAdministrator.getCanDeleteMessages();

        }
        return false;
    }

    enum ChatMemberAction {
        ADMINISTRATOR,
        LEFT,
        WARNING,
        IGNORE
    }
}