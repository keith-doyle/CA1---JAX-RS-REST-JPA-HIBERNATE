package com.example.greenhouse.dao;

import com.example.greenhouse.model.EmissionRecord;

import javax.persistence.EntityManager;
import java.util.List;

public class EmissionRecordDAO {

    public void persist(EmissionRecord rec) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(rec);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public EmissionRecord merge(EmissionRecord rec) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            EmissionRecord updated = em.merge(rec);
            em.getTransaction().commit();
            return updated;
        } finally {
            em.close();
        }
    }

    public void remove(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            EmissionRecord managed = em.find(EmissionRecord.class, id);
            if (managed != null) em.remove(managed);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public EmissionRecord findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(EmissionRecord.class, id);
        } finally {
            em.close();
        }
    }

    public List<EmissionRecord> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM EmissionRecord e", EmissionRecord.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
