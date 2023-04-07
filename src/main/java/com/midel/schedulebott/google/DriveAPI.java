package com.midel.schedulebott.google;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/* Class to demonstrate use-case of modify permissions. */
public class DriveAPI {

    static final Logger logger = LoggerFactory.getLogger(DriveAPI.class);
    public static Drive driveService;

    private static void getDriveService() {
        try {
            while(driveService == null) {
                driveService = GoogleAPIService.getDriveService();
                if (driveService == null) {
                    TimeUnit.SECONDS.sleep(10);
                    logger.error("Failed to get sheet service.");
                }
            }
        } catch (InterruptedException | IOException | GeneralSecurityException e) {
            logger.error("Error while getting sheet service.", e);
        }
    }

    public static void shareWritePermission(String fileId, String userEmailAddress) {
        try {
            getDriveService();

            JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    logger.error("Failed to share permission to {} for {}: {}", fileId, userEmailAddress, e.getMessage());
                }

                @Override
                public void onSuccess(Permission permission, HttpHeaders responseHeaders) {}
            };
            BatchRequest batch = Objects.requireNonNull(driveService).batch();

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
            logger.error("Unable to modify permission to {}: {}", userEmailAddress, e.getDetails());
        } catch (IOException e){
            logger.error("Unable to modify permission to {}: {}", userEmailAddress, e);
        }

    }
}
