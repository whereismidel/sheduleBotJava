package com.midel.schedulebott.keyboard.reply;

import com.google.common.collect.ImmutableMap;
import com.midel.schedulebott.telegram.SendMessage;

/**
 * Container of the {@link ReplyKeyboardAnswer}, which are using for handling telegram reply keyboard.
 */
public class ReplyKeyboardContainer {
    private final ImmutableMap<String, ReplyKeyboardCommand> answerMap;
    private final ReplyKeyboardCommand unknownCommand;

    public ReplyKeyboardContainer() {

        SendMessage sendMessage = new SendMessage();

        answerMap = ImmutableMap.<String, ReplyKeyboardCommand>builder()
                .put(ReplyKeyboardAnswer.HELP.getUserAnswer(), new HelpReplyKeyboardCommand(sendMessage))
                .put(ReplyKeyboardAnswer.LEADER_MENU.getUserAnswer(), new LeaderMenuReplyKeyboardCommand(sendMessage))
                .put(ReplyKeyboardAnswer.UNKNOWN.getUserAnswer(), new UnknownReplyKeyboardCommand(sendMessage))
                .build();

        unknownCommand = new UnknownReplyKeyboardCommand(sendMessage);
    }

    public ReplyKeyboardCommand retrieveCommand(String commandIdentifier) {

        return answerMap.getOrDefault(commandIdentifier, unknownCommand);
    }
}
