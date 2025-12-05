package com.example.greenhouse.dao;

import com.example.greenhouse.model.Country;

import javax.persistence.EntityManager;
import java.util.List;

public class CountryDAO {

    public void persist(Country c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Country findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public List<Country> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Country c", Country.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
