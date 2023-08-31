package com.midel.keyboard.inline;

public enum InlineKeyboardAnswer {

    IM_LEADER("Я староста групи", "im_leader"),
    IM_NOT_LEADER("Я не староста групи", "im_not_leader"),
    LEADER_MENU_OFF_SCHEDULE("Відправка розкладу: On","leader_menu_off_schedule"), // натиснення змінює стан на протилежний
    LEADER_MENU_ON_SCHEDULE("Відправка розкладу: Off","leader_menu_on_schedule"),
    LEADER_MENU_CHECK_SCHEDULE("Перевірка коректності таблиць","leader_menu_check_schedule"),
    LEADER_MENU_TEST_SCHEDULE("Тест відправки розкладу(флуд)","leader_menu_test_schedule"), // ToDo
    LEADER_MENU_CHANGE_LEADER("Передати керівництво","leader_menu_change_leader"), // ToDo
    LEADER_MENU_GROUP_INFO("Інформація про групу","leader_menu_group_info"),
    LEADER_MENU_SCHEDULE_INFO("Інформація про відправку","leader_menu_schedule_info"),
    GROUP_CHANGE_STUDENT_YES("Змінити групу","change_group_yes"),
    GROUP_CHANGE_STUDENT_NO("Залишитись в цій","change_group_no"),
    DELETE_ACCOUNT_YES("Так, видалити аккаунт", "delete_student_yes"),
    DELETE_ACCOUNT_NO("Ні, не видаляти", "delete_student_no"),
    JOIN_TO_QUEUE("Стати в чергу", "join_to_queue"),
    GO_DOWN_QUEUE("Пропустити одного вперед", "go_down_queue"),
    LEAVE_FROM_QUEUE("Покинути чергу", "leave_from_queue"),
    ARROW_NEXT_DAY(">", "next_day"),
    ARROW_PREVIOUS_DAY("<", "previous_day"),
    ARROW_CURRENT_DAY("Сьогодні", "current_day"),
    CHOOSE_FACULTY_AND_GROUP("Факультет/Група", "choose_faculty_and_group");



    private final String callbackText;
    private final String callbackData;

    InlineKeyboardAnswer(String callbackText, String callbackData) {
        this.callbackText = callbackText;
        this.callbackData = callbackData;
    }

    public String getCallbackText() {
        return callbackText;
    }

    public String getCallbackData() {
        return callbackData;
    }
}
