package com.example.fitness.dao;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
    private static final String PU_NAME = "CommunityFitnessCenterPU";
    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(PU_NAME);
        }
        return emf;
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}