package com.midel.schedulebott.google;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* Class to demonstrate use-case of modify permissions. */
public class DriveAPI {

    static final Logger logger = LoggerFactory.getLogger(DriveAPI.class);
    public static Drive driveService;
    public static void shareWritePermission(String fileId, String userEmailAddress)
            throws IOException, GeneralSecurityException {

        if (driveService == null){
            driveService = GoogleAPIService.getDriveService();
        }

        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                logger.error("Failed to share permission to {} for {}: {}", fileId, userEmailAddress, e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission, HttpHeaders responseHeaders) {}
        };
        BatchRequest batch = Objects.requireNonNull(driveService).batch();

        try {
            Permission userPermission = new Permission()
                    .setType("user")
                    .setRole("writer");

            userPermission.setEmailAddress(userEmailAddress);

            driveService.permissions().create(fileId,userPermission)
                    .setFields("id")
                    .queue(batch,callback);

            batch.execute();

            logger.info("Successfully added WRITE rights to the {}", userEmailAddress);
        } catch (GoogleJsonResponseException e) {
            logger.error("Unable to modify permission to {}: {}",userEmailAddress, e.getDetails());
        }

    }

    /**
     *
     * @param count Кількість файлів, які потрібно відобразити, -1 відобазити всі.
     * @return Повертає ArrayList з Pair<назва файлу, id файлу>
     */
    public static ArrayList<Pair<String,String>> getFileOnDisk(int count) throws GeneralSecurityException, IOException {
        if (driveService == null){
            driveService = GoogleAPIService.getDriveService();
        }

        List<File> files = driveService.files().list()
                .setFields("nextPageToken, files(id, name)")
                .execute().getFiles();

        if (files == null || files.isEmpty()) {
            return new ArrayList<>(Collections.singletonList(new Pair<>("No files found.", "-1")));
        } else {
            ArrayList<Pair<String, String>> fileList = new ArrayList<>();

            for (File file : files) {
                fileList.add(new Pair<>(file.getName(), file.getId()));
            }
            return fileList;
        }
    }

    public static boolean deleteFile(String fileId){

        try {
            if (driveService == null){
                driveService = GoogleAPIService.getDriveService();
            }

            driveService.files().delete(fileId).execute();
            return true;
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Failed to delete file with id={} : ", fileId, e);
            return false;
        }
    }

}
