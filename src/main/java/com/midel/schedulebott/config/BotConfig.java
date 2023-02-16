package com.midel.schedulebott.config;

public class BotConfig {
    public static final String BOT_USERNAME = System.getenv().get("BOT_NAME");
    public static final String BOT_TOKEN =  System.getenv().get("BOT_TOKEN");

    public static final String PRIVATE_KEY_ID = System.getenv().get("PRIVATE_KEY_ID");
    public static final String PRIVATE_KEY = System.getenv().get("PRIVATE_KEY");
    public static final String CLIENT_ID = System.getenv().get("CLIENT_ID");
    public static final String CLIENT_CERT_URL = System.getenv().get("CLIENT_CERT_URL");
    public static final String GOOGLE_CREDENTIALS =
            String.format("{"
                + "\"type\": \"service_account\","
                + "\"project_id\": \"schedulebot-365803\","
                + "\"private_key_id\": \"%s\","
                + "\"private_key\": \"%s\","
                + "\"client_email\": \"shedule-bot@schedulebot-365803.iam.gserviceaccount.com\","
                + "\"client_id\": \"%s\","
                + "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\","
                + "\"token_uri\": \"https://oauth2.googleapis.com/token\","
                + "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\","
                + "\"client_x509_cert_url\": \"%s\""
            +"}", PRIVATE_KEY_ID, PRIVATE_KEY, CLIENT_ID, CLIENT_CERT_URL);
}
