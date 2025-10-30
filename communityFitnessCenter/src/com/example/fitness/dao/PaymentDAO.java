package com.example.fitness.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.example.fitness.model.Payment;

public class PaymentDAO {

    protected static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("CommunityFitnessCenterPU");

    public PaymentDAO() { }

    public void persist(Payment payment) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(payment);
        em.getTransaction().commit();
        em.close();
    }

    public Payment merge(Payment payment) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Payment updated = em.merge(payment);
        em.getTransaction().commit();
        em.close();
        return updated;
    }

    public void remove(Payment payment) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(payment));
        em.getTransaction().commit();
        em.close();
    }

    public Payment findById(int id) {
        EntityManager em = emf.createEntityManager();
        Payment p = em.find(Payment.class, id);
        em.close();
        return p;
    }

    public List<Payment> getAllPayments() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Payment> list = new ArrayList<Payment>();
        list = em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
        em.getTransaction().commit();
        em.close();
        return list;
    }

    public List<Payment> getPaymentsForMember(int memberId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Payment> list = new ArrayList<Payment>();
        list = em.createQuery("SELECT p FROM Payment p WHERE p.member.id = :mid", Payment.class)
                 .setParameter("mid", memberId)
                 .getResultList();
        em.getTransaction().commit();
        em.close();
        return list;
    }
}