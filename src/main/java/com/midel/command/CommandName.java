package com.midel.command;

/**
 * Enumeration for {@link Command}'s.
 */
public enum CommandName {

    TEST("/test"),
    START("/start"),
    MENU("/menu"),
    HELP("/help"),
    ADMIN_HELP("/ahelp"),
    NO("no_command"),
    FLOOD("flood"),
    UNKNOWN("unknown"),
    SWITCH_SCHEDULE("/switchschedule"),
    SWITCH_DEBUG("/switchdebug"),
    GET_LESSON("/getlesson"),
    GET_SCHEDULE_FOR("/schedulefor"),
    SEND_MESSAGE("/sendmessage"),
    GET_SCHEDULE_USER("/sch"),
    SET_GROUP("/setgroup"),
    DELETE_STUDENT("/deletestudent"),
    RECREATE_STUDENT("/restart"),
    IMPORT_STUDENTS("/studentimport"),
    IMPORT_GROUPS("/groupimport"),
    IGNORE_COMMAND("ignore"),
    CREATE_QUEUE("/newqueue"),
    ADD_TO_QUEUE("/addtoqueue"),
    REMOVE_FROM_QUEUE("/removefromqueue");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

}