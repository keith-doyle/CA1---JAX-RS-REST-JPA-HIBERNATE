package com.example.fitness.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.example.fitness.model.MembershipPlan;

public class MembershipPlanDAO {

    public MembershipPlanDAO() { }

   
    public void persist(MembershipPlan plan) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager(); 
        try {
            em.getTransaction().begin();
            em.persist(plan);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    
    public void remove(MembershipPlan plan) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(plan));  
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
 
    public MembershipPlan merge(MembershipPlan plan) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager(); 
        try {
            em.getTransaction().begin();
            MembershipPlan updatedPlan = em.merge(plan);
            em.getTransaction().commit();
            return updatedPlan;
        } finally {
            em.close();
        }
    }

    
    public MembershipPlan findPlanById(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager(); 
        try {
            return em.find(MembershipPlan.class, id);
        } finally {
            em.close();
        }
    }

    public List<MembershipPlan> getAllPlans() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM MembershipPlan p", MembershipPlan.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
