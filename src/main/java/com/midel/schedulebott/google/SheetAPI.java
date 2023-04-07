package com.midel.schedulebott.google;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SheetAPI {

    public static final Logger logger = LoggerFactory.getLogger(SheetAPI.class);
    public static Sheets sheetService;

    private static void getSheetService() {
        try {
            while(sheetService == null) {
                sheetService = GoogleAPIService.getSheetService();
                if (sheetService == null) {
                    TimeUnit.SECONDS.sleep(10);
                    logger.error("Failed to get sheet service.");
                }
            }
        } catch (InterruptedException | IOException | GeneralSecurityException e) {
            logger.error("Error while getting sheet service.", e);
        }
    }

    public static List<List<Object>> readSheetForRange(String spreadsheetId, String range) {

        try {
            getSheetService();
            ValueRange response = sheetService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            return response.getValues();

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            logger.error("Failed read data for range: {}", error);
        } catch (IOException ee){
            logger.error("Error while reading sheet for range. SheetID = {}, Range = {}", spreadsheetId, range, ee);
        }

        return null;
    }



    public static boolean updateValues(String spreadsheetId, String range, List<List<Object>> values) {

        try {
            getSheetService();

            ValueRange vr = new ValueRange()
                    .setValues(values)
                    .setMajorDimension("ROWS");

            sheetService
                    .spreadsheets()
                    .values()
                    .update(spreadsheetId, range, vr)
                    .setValueInputOption("USER_ENTERED").execute();
            return true;
        } catch (IOException e){
            logger.error("Error while updating sheet for range. SheetID = {}, Range = {}", spreadsheetId, range, e);
            return false;
        }
    }
    public static String createSpreadsheetFromTemplateAndSharePermission(String newSheetTitle, String[] shareList, String templateSheetId) {
        try {
            getSheetService();

            // Export template spreadsheet
            Spreadsheet sourceSpreadsheet = sheetService
                    .spreadsheets()
                    .get(templateSheetId)
                    .setIncludeGridData(true)
                    .execute();

            // Create a new spreadsheet based on template
            Spreadsheet spreadsheetClone = sourceSpreadsheet.clone();
            spreadsheetClone.setSpreadsheetId("").setSpreadsheetUrl("").getProperties().setTitle(newSheetTitle);

            Spreadsheet destinationSpreadsheet = sheetService
                    .spreadsheets()
                    .create(spreadsheetClone)
                    .setPrettyPrint(true)
                    .execute();

            logger.info("Spreadsheet successfully created and filled from template: {}", destinationSpreadsheet.getSpreadsheetUrl());

            // Give access to the new spreadsheet to users from shareList
            for (String user : shareList) {
                DriveAPI.shareWritePermission(destinationSpreadsheet.getSpreadsheetId(), user);
            }

            return destinationSpreadsheet.getSpreadsheetId();

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                logger.error("Spreadsheet not found with id {}", templateSheetId, e);
            } else {
                logger.error("Unknown error while creating and filling new sheet: ", e);
            }
        } catch (Exception e){
            logger.error("Error while parsing from/to sheet or failed to update sheet title or protected range.", e);
        }

        return null;
    }

}