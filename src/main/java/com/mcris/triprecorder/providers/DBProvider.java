package com.mcris.triprecorder.providers;

import com.mcris.triprecorder.models.entities.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.sql.Timestamp;
import java.util.UUID;

public class DBProvider {
    private static DBProvider instance = null;

    public static DBProvider getInstance() {
        if (instance == null) {
            instance = new DBProvider();
        }
        return instance;
    }

    private final EntityManagerFactory entityManagerFactory;

    private DBProvider() {
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    }

    private EntityManager getNewNetityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public Session getSession(String sessionToken) {
        EntityManager em = getNewNetityManager();
        try {
            return em.find(Session.class, UUID.fromString(sessionToken));
        }catch(Exception ex){
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }
}
