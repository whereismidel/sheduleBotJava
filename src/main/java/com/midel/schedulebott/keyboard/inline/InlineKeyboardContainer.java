package com.midel.schedulebott.keyboard.inline;

import com.google.common.collect.ImmutableMap;
import com.midel.schedulebott.telegram.SendMessage;

/**
 * Container of the {@link InlineKeyboardHandler}, which are using for handling telegram inline keyboard.
 */
public class InlineKeyboardContainer {

        private final ImmutableMap<String, InlineKeyboardHandler> answerMap;

        private final InlineKeyboardHandler deleteHandler;

        public InlineKeyboardContainer() {

            SendMessage sendMessage = new SendMessage();

            answerMap = ImmutableMap.<String, InlineKeyboardHandler>builder()
                    .put(InlineKeyboardAnswer.IM_LEADER.getCallbackData(), new CreateLeaderInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.IM_NOT_LEADER.getCallbackData(), new CreateStudentInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.GROUP_CHANGE_STUDENT_YES.getCallbackData(), new ChangeGroupInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.DELETE_ACCOUNT_YES.getCallbackData(), new DeleteAccountInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.LEADER_MENU_CHECK_SCHEDULE.getCallbackData(), new LeaderMenuCheckScheduleInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.LEADER_MENU_ON_SCHEDULE.getCallbackData(), new LeaderMenuSwitchScheduleInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.LEADER_MENU_OFF_SCHEDULE.getCallbackData(), new LeaderMenuSwitchScheduleInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.LEADER_MENU_GROUP_INFO.getCallbackData(), new LeaderMenuGroupInfoInlineHandler(sendMessage))
                    .put(InlineKeyboardAnswer.LEADER_MENU_SCHEDULE_INFO.getCallbackData(), new LeaderMenuScheduleInfoInlineHandler(sendMessage))
                    .build();

            deleteHandler = new DeleteMessageInlineHandler(sendMessage);
        }

        public InlineKeyboardHandler retrieveCommand(String callbackData) {
            InlineKeyboardHandler answer = answerMap.get(callbackData);

            if (answer != null){
                return answer;
            } else {
                return deleteHandler;
            }
        }
    }
