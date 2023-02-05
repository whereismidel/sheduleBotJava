package com.midel.schedulebott.config;

public class BotConfig {
    public static final String BOT_USERNAME = System.getenv().get("BOT_NAME");
    public static final String BOT_TOKEN =  System.getenv().get("BOT_TOKEN");
    public static final String GOOGLE_CREDENTIALS = System.getenv().get("BOT_CREDENTIALS");

}

