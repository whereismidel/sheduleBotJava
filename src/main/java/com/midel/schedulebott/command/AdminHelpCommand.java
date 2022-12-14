package com.midel.schedulebott.command;

import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.midel.schedulebott.command.CommandName.*;

/**
 * AdminHelp {@link Command}.
 */
public class AdminHelpCommand extends Command {

    public static final String ADMIN_HELP_MESSAGE = String.format("✨*Доступні адмін\\-команди:*✨\n\n"
                    + "`%s` \\- вімкнути\\/вимкнути режим відладки\\. \n"
                    + "`%s` \\- ввімкнути\\/вимкнути надсилання розкладу по ВСІМ каналам\\. \n"
                    + "`%s` \\- \\(debug\\) перевірити розклад\\. \n"
                    + "`%s` \\- \\(debug\\) отримати інформацію про конкретну пару\\. \n"
                    + "`%s` \\- надіслати повідомлення по групам\\. \n",
            SWITCH_DEBUG.getCommandName(), SWITCH_SCHEDULE.getCommandName(), GET_SCHEDULE_FOR.getCommandName(),
            GET_LESSON.getCommandName(), SEND_MESSAGE.getCommandName());

    public AdminHelpCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        sendMessage.sendMarkupV2Message(update.getMessage().getChatId().toString(), ADMIN_HELP_MESSAGE);
    }
}
