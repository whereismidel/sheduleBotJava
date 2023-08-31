package com.midel.command;


import com.google.common.collect.ImmutableMap;
import com.midel.command.annotation.AdminCommand;
import com.midel.command.annotation.GroupCommand;
import com.midel.command.annotation.UserCommand;
import com.midel.telegram.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
public class CommandContainer {

    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    private final Command ignoreCommand;
    private final List<String> admins;

    public CommandContainer(List<String> admins) {

        this.admins = admins;
        SendMessage sendMessage = new SendMessage();

        commandMap = ImmutableMap.<String, Command>builder()
                .put(CommandName.TEST.getCommandName(), new TestCommand(sendMessage))
                .put(CommandName.START.getCommandName(), new StartCommand(sendMessage))
                .put(CommandName.HELP.getCommandName(), new HelpCommand(sendMessage))
                .put(CommandName.NO.getCommandName(), new NoCommand(sendMessage))
                .put(CommandName.UNKNOWN.getCommandName(), new UnknownCommand(sendMessage))
                .put(CommandName.SWITCH_DEBUG.getCommandName(), new SwitchDebugCommand(sendMessage))
                .put(CommandName.SWITCH_SCHEDULE.getCommandName(), new SwitchScheduleCommand(sendMessage))
                .put(CommandName.GET_LESSON.getCommandName(), new GetLessonCommand(sendMessage))
                .put(CommandName.GET_SCHEDULE_FOR.getCommandName(), new GetScheduleForCommand(sendMessage))
                .put(CommandName.GET_SCHEDULE_USER.getCommandName(), new GetScheduleUserCommand(sendMessage))
                .put(CommandName.ADMIN_HELP.getCommandName(), new AdminHelpCommand(sendMessage))
                .put(CommandName.SEND_MESSAGE.getCommandName(), new SendMessageCommand(sendMessage))
                .put(CommandName.MENU.getCommandName(), new MenuCommand(sendMessage))
                .put(CommandName.DELETE_STUDENT.getCommandName(), new DeleteStudentCommand(sendMessage))
                .put(CommandName.SET_GROUP.getCommandName(), new SetGroupCommand(sendMessage))
                .put(CommandName.RECREATE_STUDENT.getCommandName(), new RestartCommand(sendMessage))
                .put(CommandName.IMPORT_STUDENTS.getCommandName(), new ImportStudentsCommand(sendMessage))
                .put(CommandName.IMPORT_GROUPS.getCommandName(), new ImportGroupsCommand(sendMessage))
                .put(CommandName.FLOOD.getCommandName(), new FloodCommand(sendMessage))
                .put(CommandName.IGNORE_COMMAND.getCommandName(), new IgnoreCommand(sendMessage))
                .put(CommandName.CREATE_QUEUE.getCommandName(), new CreateQueueCommand(sendMessage))
                .put(CommandName.ADD_TO_QUEUE.getCommandName(), new AddToQueueCommand(sendMessage))
                .put(CommandName.REMOVE_FROM_QUEUE.getCommandName(), new RemoveFromQueueCommand(sendMessage))
                .build();

        unknownCommand = new UnknownCommand(sendMessage);
        ignoreCommand = new IgnoreCommand(sendMessage);
    }

    public Command retrieveCommand(String commandIdentifier, List<String> arguments, Update update) {
        Command orDefault = commandMap.getOrDefault(commandIdentifier, unknownCommand);

        if (orDefault != null) {
            orDefault.arguments = arguments;

            if (isAdminCommand(orDefault)) {
                if (admins.contains(update.getMessage().getFrom().getId().toString())) {
                    return orDefault;
                } else {
                    return unknownCommand;
                }
            }

            if (isGroupCommand(orDefault)) {
                if (isUserCommand(orDefault) || update.getMessage().isGroupMessage() || update.getMessage().isSuperGroupMessage()){
                    return orDefault;
                } else {
                    return unknownCommand;
                }
            }

            if (isUserCommand(orDefault)) {
                if (!update.getMessage().isGroupMessage() && !update.getMessage().isSuperGroupMessage() && !update.getMessage().isChannelMessage()){
                    return orDefault;
                } else {
                    return unknownCommand;
                }
            }
        }
        return ignoreCommand;
    }

    private boolean isAdminCommand(Command command) {
        return nonNull(command.getClass().getAnnotation(AdminCommand.class));
    }

    private boolean isGroupCommand(Command command) {
        return nonNull(command.getClass().getAnnotation(GroupCommand.class));
    }

    private boolean isUserCommand(Command command) {
        return nonNull(command.getClass().getAnnotation(UserCommand.class));
    }
}