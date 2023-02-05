package com.midel.schedulebott.command;

import com.midel.schedulebott.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.midel.schedulebott.command.CommandName.*;


/**
 * Help {@link Command}.
 */
public class HelpCommand extends Command {

    public static final String HELP_MESSAGE = String.format("✨*Доступні команди*✨\n\n"
                    + "%s \\- повернутись в головне меню\\. \n"
                    + "%s \\- допомога по командам\\. \n"
                    + "`%s` \\- отримати розклад на конкретний день\\. \n"
                    + "%s \\- встановити/змінити групу\\. \n"
                    + "%s \\- видалити аккаунт\\. \n",
            MENU.getCommandName(), HELP.getCommandName(), GET_SCHEDULE_USER.getCommandName(),
            SET_GROUP.getCommandName(), RECREATE_STUDENT.getCommandName());

    public HelpCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        sendMessage.sendMarkupV2Message(update.getMessage().getChatId().toString(), HELP_MESSAGE);
    }
}