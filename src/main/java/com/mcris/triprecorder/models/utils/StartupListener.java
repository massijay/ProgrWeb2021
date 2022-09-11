package com.mcris.triprecorder.models.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;

@WebListener
public class StartupListener implements ServletContextListener {
    private static final long CLEANUP_INTERVAL_MILLIS = 30 * 60 * 1000;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        Timer cleanupTimer = new Timer();
        cleanupTimer.schedule(new ClearExpiredSessionsTask(), 0, CLEANUP_INTERVAL_MILLIS);
    }
}
