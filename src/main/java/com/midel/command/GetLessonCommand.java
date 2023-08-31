package com.midel.command;

import com.midel.command.annotation.AdminCommand;
import com.midel.config.ChatConfig;
import com.midel.schedule.ScheduledTask;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static com.midel.command.CommandName.GET_LESSON;
import static com.midel.command.CommandName.NO;
import static com.midel.config.ChatConfig.debug;

/**
 * GetLesson {@link Command}.
 */
@AdminCommand
public class  GetLessonCommand extends Command{

    public final static String INVALID_ARGUMENT_MESSAGE = "Невірно вказані аргументи або їх кількість або виключений режим відладки. \n"
                                                            +"/getLesson - для довідки.";
    public final static String INFO_MESSAGE = "`/getLesson` `назва групи` `номер тижня\\(1\\/2\\)` `день тижня` `номер пари` \\- відобразити інформацію про пару по вказаним значенням\\.";
    public GetLessonCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
//        String userId = update.getMessage().getChat().getId().toString();
        String userId = "-1001833464949";

        if (arguments != null) {
            if (arguments.size() == 0) {
                    sendMessage.sendMarkupV2Message(userId, INFO_MESSAGE);
            } else if (arguments.size() != 4 || !debug){
                sendMessage.sendHTMLMessage(userId, INVALID_ARGUMENT_MESSAGE);
            } else {
                ChatConfig.debugArray = new ArrayList<>();
                ChatConfig.debugArray.add(GET_LESSON);

                ChatConfig.debugArray.add(arguments.get(0).toUpperCase()); // назва групи
                for(int i = 1; i <= 2; i++)
                    for (int j = 1; j <= 7; j++)
                        for (int k = 1; k <= 6; k++) {
                            ChatConfig.debugArray = new ArrayList<>();
                            ChatConfig.debugArray.add(GET_LESSON);
                            ChatConfig.debugArray.add("БІ-344Б"); // назва групи
                            ChatConfig.debugArray.add(i+""); // номер тижня(1/2)
                            ChatConfig.debugArray.add(j+""); // день тижня(число)
                            ChatConfig.debugArray.add(k+""); // номер пари

                            new ScheduledTask().checkAvailabilityOfLessonsEveryDay();
                        }
//                ChatConfig.debugArray.add(arguments.get(0).toUpperCase()); // назва групи
//                ChatConfig.debugArray.add(arguments.get(1).toLowerCase()); // номер тижня(1/2)
//                ChatConfig.debugArray.add(arguments.get(2).toLowerCase()); // день тижня(число)
//                ChatConfig.debugArray.add(arguments.get(3)); // номер пари
//
//                new ScheduledTask().checkAvailabilityOfLessonsEveryDay();
            }
        } else {
            sendMessage.sendHTMLMessage(userId, NO.getCommandName());
        }
    }
}
