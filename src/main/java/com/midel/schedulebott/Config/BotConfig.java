package com.midel.schedulebott.Config;

public class BotConfig {
    public static boolean test = false;
    public static final String BOT_USERNAME = System.getenv().get("BOT_NAME");
    public static final String BOT_TOKEN =  System.getenv().get("BOT_TOKEN");;
}

