package com.midel.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.midel.config.BotConfig;
import com.midel.exceptions.MissingCredentialFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;


public class GoogleAPIService {
    static final Logger logger = LoggerFactory.getLogger(GoogleAPIService.class);
    private static final String APPLICATION_NAME = "ScheduleBot";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);

    private static GoogleCredentials authorize() throws IOException, MissingCredentialFileException {
        GoogleCredentials googleCredentials;

   //     try (InputStream inputSteam = GoogleAPIService.class.getResourceAsStream("/credentials_service.json")) {
        try(InputStream inputSteam = Files.newInputStream(new File(BotConfig.GOOGLE_CREDENTIALS).toPath())) {
            googleCredentials = GoogleCredentials.fromStream(inputSteam).createScoped(SCOPES);
        }

        return googleCredentials;
    }

    public static Sheets getSheetService() throws IOException, GeneralSecurityException{

        GoogleCredentials googleCredentials;
        try {
            googleCredentials = authorize();
        } catch (MissingCredentialFileException e) {
            logger.error("Missing credentials_service.json file.");

            return null;
        }

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(googleCredentials)
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {

        GoogleCredentials googleCredentials;
        try {
            googleCredentials = authorize();
        } catch (MissingCredentialFileException e) {
            logger.error("Missing credentials_service.json file.");

            return null;
        }

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(googleCredentials)
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}