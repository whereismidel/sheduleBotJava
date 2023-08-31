package com.midel.command;

import com.midel.command.annotation.GroupCommand;
import com.midel.command.annotation.UserCommand;
import com.midel.config.BotConfig;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.midel.command.CommandName.*;


/**
 * Help {@link Command}.
 */
@UserCommand
@GroupCommand
public class HelpCommand extends Command {

    public static final String PRIVATE_HELP_MESSAGE = String.format("✨*Доступні команди*✨\n\n"
                    + MENU.getCommandName() + " \\- повернутись в головне меню\\. \n"
                    + HELP.getCommandName() + " \\- допомога по командам\\. \n"
                    + (CommandContainer.isDisabledCommand(new GetScheduleUserCommand(null))? "" : GET_SCHEDULE_USER.getCommandName() + " \\- отримати розклад на конкретний день\\. \n")
                    + (CommandContainer.isDisabledCommand(new SetGroupCommand(null))? "" : SET_GROUP.getCommandName() + " \\- встановити/змінити групу\\. \n")
                    + (CommandContainer.isDisabledCommand(new RestartCommand(null))? "" : RECREATE_STUDENT.getCommandName() + " \\- видалити аккаунт\\. \n")
    );

    public static final String GROUP_HELP_MESSAGE = String.format("✨<b>Доступні команди</b>✨\n\n"
                    + HELP.getCommandName() + "@" + BotConfig.BOT_USERNAME + " - допомога по командам. \n"
                    + (CommandContainer.isDisabledCommand(new GetScheduleUserCommand(null))? "" : "<code>" + GET_SCHEDULE_USER.getCommandName() +"@" + BotConfig.BOT_USERNAME + "</code> - отримати розклад на конкретний день. \n")
                    + (CommandContainer.isDisabledCommand(new SetGroupCommand(null))? "" : SET_GROUP.getCommandName() + "@" + BotConfig.BOT_USERNAME + " - встановити/змінити групу. \n")
                    + (CommandContainer.isDisabledCommand(new CreateQueueCommand(null))? "" : CREATE_QUEUE.getCommandName() + "@" + BotConfig.BOT_USERNAME + " - створити чергу. \n")
    );

    public HelpCommand(SendMessage sendMessage){
        super(sendMessage);
    }

    @Override
    public void execute(Update update) {
        if (update.getMessage().isUserMessage())
            sendMessage.sendMarkupV2Message(update.getMessage().getChatId().toString(), PRIVATE_HELP_MESSAGE);
        else
            sendMessage.sendHTMLMessage(update.getMessage().getChatId().toString(), GROUP_HELP_MESSAGE);
    }
}