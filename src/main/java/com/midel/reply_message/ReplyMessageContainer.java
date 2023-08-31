package com.midel.reply_message;

import com.google.common.collect.ImmutableMap;
import com.midel.keyboard.reply.ReplyKeyboardAnswer;
import com.midel.telegram.SendMessage;

/**
 * Container of the {@link ReplyKeyboardAnswer}, which are using for handling telegram reply keyboard.
 */
public class ReplyMessageContainer {


    private final ImmutableMap<String, ReplyMessage> messageMap;
    private final ReplyMessage unknownCommand;

    public ReplyMessageContainer() {

        SendMessage sendMessage = new SendMessage();

        messageMap = ImmutableMap.<String, ReplyMessage>builder()
//                .put(ReplyMessageKey.SET_GROUP.getKeyMessage(), new CreateAndSetGroupNameReplyMessage(sendMessage))
                .put(ReplyMessageKey.UNKNOWN_REPLY.getKeyMessage(), new UnknownReplyMessage(sendMessage))
                .put(ReplyMessageKey.CREATE_CHANNEL.getKeyMessage(), new CreateChannelReplyMessage(sendMessage))
                .put(ReplyMessageKey.CREATE_AND_SHARE_SHEET.getKeyMessage(), new CreateSheetAndShareEmailReplyMessage(sendMessage))
                .put(ReplyMessageKey.DELETE_STUDENT.getKeyMessage(), new DeleteStudentReplyMessage(sendMessage))
                .put(ReplyMessageKey.DELETE_LEADER.getKeyMessage(), new DeleteLeaderAccountReplyMessage(sendMessage))
                .put(ReplyMessageKey.CREATE_STUDENT.getKeyMessage(), new CreateStudentReplyMessage(sendMessage))
                .build();

        unknownCommand = new UnknownReplyMessage(sendMessage);
    }

    public ReplyMessage retrieveCommand(String replyKey) {

        return messageMap.getOrDefault(replyKey, unknownCommand);
    }
}
