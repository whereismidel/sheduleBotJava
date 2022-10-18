package com.midel.schedulebott.Config;

import java.util.ArrayList;

public class ChatConfig {
    // debug mode
    public static final Long ADMIN_ID = 787943933L;
    public static boolean debug = false;
    public static ArrayList<Integer> debugArray= new ArrayList<>();

    // Global setting if there are fatal errors
    public static boolean sendSchedule = true;

    // за скільки до початку пари повідомляти про неї.
    public static int scheduleTime = 5; // якщо змінювати - слідкувати, щоб крон вираз був кратний по хвилинам.

    public static String getAdminId(){
        return ADMIN_ID+"";
    }
}
