package com.mcris.triprecorder.models.utils;

import com.mcris.triprecorder.providers.DBProvider;

import java.time.Instant;
import java.util.TimerTask;

public class ClearExpiredSessionsTask extends TimerTask {
    @Override
    public void run() {
        System.out.println(Instant.now().toString() + " Cleaning expired sessions...");
        DBProvider.getInstance().cleanupExpiredSessions();
    }
}
