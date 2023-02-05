package com.midel.schedulebott.command;


import com.google.common.collect.ImmutableMap;
import com.midel.schedulebott.command.annotation.AdminCommand;
import com.midel.schedulebott.telegram.SendMessage;

import java.util.List;

import static com.midel.schedulebott.command.CommandName.*;
import static java.util.Objects.nonNull;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
public class CommandContainer {

    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    private final List<String> admins;

    public CommandContainer(List<String> admins) {

        this.admins = admins;
        SendMessage sendMessage = new SendMessage();

        commandMap = ImmutableMap.<String, Command>builder()
                .put(TEST.getCommandName(), new TestCommand(sendMessage))
                .put(START.getCommandName(), new StartCommand(sendMessage))
                .put(HELP.getCommandName(), new HelpCommand(sendMessage))
                .put(NO.getCommandName(), new NoCommand(sendMessage))
                .put(UNKNOWN.getCommandName(), new UnknownCommand(sendMessage))
                .put(SWITCH_DEBUG.getCommandName(), new SwitchDebugCommand(sendMessage))
                .put(SWITCH_SCHEDULE.getCommandName(), new SwitchScheduleCommand(sendMessage))
                .put(GET_LESSON.getCommandName(), new GetLessonCommand(sendMessage))
                .put(GET_SCHEDULE_FOR.getCommandName(), new GetScheduleForCommand(sendMessage))
                .put(GET_SCHEDULE_USER.getCommandName(), new GetScheduleUserCommand(sendMessage))
                .put(ADMIN_HELP.getCommandName(), new AdminHelpCommand(sendMessage))
                .put(SEND_MESSAGE.getCommandName(), new SendMessageCommand(sendMessage))
                .put(MENU.getCommandName(), new MenuCommand(sendMessage))
                .put(DELETE_STUDENT.getCommandName(), new DeleteStudentCommand(sendMessage))
                .put(SET_GROUP.getCommandName(), new SetGroupCommand(sendMessage))
                .put(RECREATE_STUDENT.getCommandName(), new RestartCommand(sendMessage))
                .put(IMPORT_STUDENTS.getCommandName(), new ImportStudentsCommand(sendMessage))
                .put(IMPORT_GROUPS.getCommandName(), new ImportGroupsCommand(sendMessage))
                .put(FLOOD.getCommandName(), new FloodCommand(sendMessage))
                .build();

        unknownCommand = new UnknownCommand(sendMessage);
    }

    public Command retrieveCommand(String commandIdentifier, List<String> arguments, String userId) {
        Command orDefault = commandMap.getOrDefault(commandIdentifier, unknownCommand);

        if (orDefault != null) {
            orDefault.arguments = arguments;

            if (isAdminCommand(orDefault)) {
                if (admins.contains(userId)) {
                    return orDefault;
                } else {
                    return unknownCommand;
                }
            }
        }
        return orDefault;
    }

    private boolean isAdminCommand(Command command) {
        return nonNull(command.getClass().getAnnotation(AdminCommand.class));
    }
}