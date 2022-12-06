package com.midel.schedulebott.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;


public class GoogleAPIService {
    private static final String APPLICATION_NAME = "ScheduleBot";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);

    private static GoogleCredentials authorize() throws IOException, GeneralSecurityException {
        GoogleCredentials googleCredentials;

        try (InputStream inputSteam = GoogleAPIService.class.getResourceAsStream("/credentials_service.json")) {
            googleCredentials = GoogleCredentials.fromStream(inputSteam).createScoped(SCOPES);
        }

        return googleCredentials;
    }

    public static Sheets getSheetService() throws IOException, GeneralSecurityException {

        GoogleCredentials googleCredentials = authorize();

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(googleCredentials)
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {

        GoogleCredentials googleCredentials = authorize();

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(googleCredentials)
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}