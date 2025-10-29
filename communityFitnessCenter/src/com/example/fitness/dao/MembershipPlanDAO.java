package com.example.fitness.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.example.fitness.model.MembershipPlan;

public class MembershipPlanDAO {
    
    protected static EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory("CommunityFitnessPU");

    public MembershipPlanDAO() {
    }
    
    // Create / Persist
    public void persist(MembershipPlan plan) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(plan);
        em.getTransaction().commit();
        em.close();
    }

    // Remove / Delete
    public void remove(MembershipPlan plan) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(plan));  // merge ensures managed instance
        em.getTransaction().commit();
        em.close();
    }

    // Update / Merge
    public MembershipPlan merge(MembershipPlan plan) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        MembershipPlan updatedPlan = em.merge(plan);
        em.getTransaction().commit();
        em.close();
        return updatedPlan;
    }

    

    // Find by ID
    public MembershipPlan findPlanById(int id) {
        EntityManager em = emf.createEntityManager();
        MembershipPlan plan = em.find(MembershipPlan.class, id);
        em.close();
        return plan;
    }

    // Read all
    public List<MembershipPlan> getAllPlans() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<MembershipPlan> plans = em.createQuery("SELECT p FROM MembershipPlan p", MembershipPlan.class)
                                       .getResultList();
        em.getTransaction().commit();
        em.close();
        return plans;
    }
    
}

	