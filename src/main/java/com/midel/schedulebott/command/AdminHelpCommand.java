package com.midel.schedulebott.command;

import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.config.BotConfig;
import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.midel.schedulebott.command.CommandName.*;

/**
 * AdminHelp {@link Command}.
 */
@AdminCommand
public class AdminHelpCommand extends Command {

    public static final String ADMIN_HELP_MESSAGE = String.format("✨*Доступні адмін\\-команди:*✨\n\n"
                    + "%s \\- вімкнути\\/вимкнути режим відладки\\. \n"
                    + "%s \\- ввімкнути\\/вимкнути надсилання розкладу по ВСІМ каналам\\. \n"
                    + "%s \\- \\(debug\\) перевірити розклад\\. \n"
                    + "%s \\- \\(debug\\) отримати інформацію про конкретну пару\\. \n"
                    + "%s \\- надіслати повідомлення по групам\\. \n"
                    + "%s \\- видалити студента\\. \n"
                    + "%s \\- примусово імпортувати таблицю \\\"Користувачі\\\"\\. \n"
                    + "%s \\- примусово імпортувати таблицю \\\"Налаштування груп\\\"\\. \n"
                    + "%s@" + BotConfig.BOT_USERNAME + " \\- додати до черги користувача\\(групова команда з відміткою повідомлення\\)"
                    + "%s@" + BotConfig.BOT_USERNAME + " \\- видалити з черги користувача\\(групова команда з відміткою повідомлення\\)",
            SWITCH_DEBUG.getCommandName(), SWITCH_SCHEDULE.getCommandName(), GET_SCHEDULE_FOR.getCommandName(),
            GET_LESSON.getCommandName(), SEND_MESSAGE.getCommandName(), DELETE_STUDENT.getCommandName(),
            IMPORT_STUDENTS.getCommandName(), IMPORT_GROUPS.getCommandName(), ADD_TO_QUEUE.getCommandName(), REMOVE_FROM_QUEUE.getCommandName());

    public AdminHelpCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        sendMessage.sendMarkupV2Message(update.getMessage().getChatId().toString(), ADMIN_HELP_MESSAGE);
    }
}
