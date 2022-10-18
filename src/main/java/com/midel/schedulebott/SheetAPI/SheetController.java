package com.midel.schedulebott.SheetAPI;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SheetController {

    private static Sheets sheetsService;
    private static final String APPLICATION_NAME = "ScheduleBot";

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetController.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(Objects.requireNonNull(in)));


        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleApacheHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");

        return credential;
    }

    private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static List<List<Object>> readSheetForRange(String spreadsheetID, String range) throws IOException, GeneralSecurityException {
        if (sheetsService == null){
            sheetsService = getSheetsService();
        }
        try {
            ValueRange responce = sheetsService.spreadsheets().values()
                    .get(spreadsheetID, range)
                    .execute();
            List<List<Object>> values = responce.getValues();

            return values;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            System.out.println(error);
        }

        return null;

    }

    public void updateValues(String spreadsheetId, String range, List<List<Object>> values) throws IOException, GeneralSecurityException {
        if (sheetsService == null){
            sheetsService = getSheetsService();
        }

        ValueRange vr = new ValueRange()
                .setValues(values)
                .setMajorDimension("ROWS");

        sheetsService
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, vr)
                .setValueInputOption("USER_ENTERED").execute();
    }
}


