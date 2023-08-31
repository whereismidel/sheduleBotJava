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
                    + "%s \\- повернутись в головне меню\\. \n"
                    + "%s \\- допомога по командам\\. \n"
                    + "`%s` \\- отримати розклад на конкретний день\\. \n"
                    + "%s \\- встановити/змінити групу\\. \n"
                    + "%s \\- видалити аккаунт\\. \n",
            MENU.getCommandName(), HELP.getCommandName(), GET_SCHEDULE_USER.getCommandName(),
            SET_GROUP.getCommandName(), RECREATE_STUDENT.getCommandName());

    public static final String GROUP_HELP_MESSAGE = String.format("✨<b>Доступні команди</b>✨\n\n"
                    + "%s@" + BotConfig.BOT_USERNAME + " - допомога по командам. \n"
                    + "<code>%s@" + BotConfig.BOT_USERNAME + "</code> - отримати розклад на конкретний день. \n"
                    + "%s@" + BotConfig.BOT_USERNAME + " - встановити/змінити групу. \n"
                    + "%s@" + BotConfig.BOT_USERNAME + " - створити чергу. \n",
            HELP.getCommandName(), GET_SCHEDULE_USER.getCommandName(),
            SET_GROUP.getCommandName(), CREATE_QUEUE.getCommandName());

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