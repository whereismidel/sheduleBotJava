package com.midel.schedulebott.google;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class SheetAPI {

    static final Logger logger = LoggerFactory.getLogger(SheetAPI.class);
    private static Sheets sheetService;

    public static List<List<Object>> readSheetForRange(String spreadsheetID, String range) throws GeneralSecurityException, IOException {

        try {
            if (sheetService == null) {
                sheetService = GoogleAPIService.getSheetService();
            }
            ValueRange responce = sheetService.spreadsheets().values()
                    .get(spreadsheetID, range)
                    .execute();

            return responce.getValues();

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            logger.error("Failed read data for range: {}", error);

            return null;

        }
    }

    public static void updateValues(String spreadsheetId, String range, List<List<Object>> values) throws IOException, GeneralSecurityException {

        if (sheetService == null) {
            sheetService = GoogleAPIService.getSheetService();
        }

        ValueRange vr = new ValueRange()
                .setValues(values)
                .setMajorDimension("ROWS");

        sheetService
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, vr)
                .setValueInputOption("USER_ENTERED").execute();
    }

    public static String createSheet(String title, String[] shareList, String copyFrom) {
        try {
            if (sheetService == null) {
                sheetService = GoogleAPIService.getSheetService();
            }

            // Create new spreadsheet with a title
            Spreadsheet spreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle(title));

            spreadsheet = sheetService.spreadsheets().create(spreadsheet)
                    .setFields("spreadsheetId")
                    .execute();

            for (String user : shareList) {
                DriveAPI.shareWritePermission(spreadsheet.getSpreadsheetId(), user);
            }

            copySheet(copyFrom, spreadsheet.getSpreadsheetId());

            logger.info("Spreadsheet successfully created and filled: https://docs.google.com/spreadsheets/d/{}", spreadsheet.getSpreadsheetId());
            return spreadsheet.getSpreadsheetId();
        } catch (Exception e){
            logger.warn("Error when sheet is creating.", e);

            return null;
        }

    }

    private static void copySheet(String copyFrom, String copyTo) throws GeneralSecurityException, IOException {
        try {
            if (sheetService == null) {
                sheetService = GoogleAPIService.getSheetService();
            }

            List<Sheet> sheets = sheetService
                    .spreadsheets()
                    .get(copyFrom)
                    .execute()
                    .getSheets();

            CopySheetToAnotherSpreadsheetRequest requestBody =
                    new CopySheetToAnotherSpreadsheetRequest()
                            .setDestinationSpreadsheetId(copyTo);

            for (Sheet sheet : sheets) {
                sheetService
                        .spreadsheets()
                        .sheets()
                        .copyTo(copyFrom, sheet.getProperties().getSheetId(), requestBody)
                        .execute();
            }

            copySheetTitle(copyFrom, copyTo);
            logger.info("Template table successfully cloned to {}", copyTo);
        } catch (Exception e){
            logger.error("Error when sheet is copping. From {} to {}", copyFrom, copyTo, e);
        }
    }

    private static void copySheetTitle(String copyFrom, String copyTo)
            throws GeneralSecurityException, IOException {

        if (sheetService == null) {
            sheetService = GoogleAPIService.getSheetService();
        }

        // Get spreadsheet data
        Sheets.Spreadsheets.Get request = sheetService.spreadsheets().get(copyFrom);
        List<Sheet> sheets = request.execute().getSheets();

        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetResponse response = null;
        try {
            // Delete empty first sheet
            requests.add(new Request()
                    .setDeleteSheet(new DeleteSheetRequest()
                            .setSheetId(sheets.get(0)
                                    .getProperties()
                                    .getSheetId())));
            // Change title from "copy of Розклад" to "РОЗКЛАД";
            requests.add(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties()
                                    .setSheetId(sheets.get(1).getProperties().getSheetId())
                                    .setTitle(sheets.get(1).getProperties().getTitle()))
                            .setFields("title")));
            // Change title from "copy of Розклад" to "РОЗКЛАД";
            requests.add(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties()
                                    .setSheetId(sheets.get(2).getProperties().getSheetId())
                                    .setTitle(sheets.get(1).getProperties().getTitle()))
                            .setFields("title")));

            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);

            sheetService.spreadsheets().batchUpdate(copyTo, body).execute();
            logger.info("Sheet title table successfully cloned from {} to {}", copyFrom, copyTo);
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                logger.error("Spreadsheet not found with id '{}' or '{}'.", copyFrom, copyTo, e);
            } else {
                logger.error("Failed to copy sheet title.", e);
            }
        }

    }
}


