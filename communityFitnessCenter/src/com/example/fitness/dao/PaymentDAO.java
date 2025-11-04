package com.example.fitness.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import com.example.fitness.model.Payment;

public class PaymentDAO {

    public PaymentDAO() { }

    public void persist(Payment payment) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(payment);
        em.getTransaction().commit();
        em.close();
    }

    public Payment merge(Payment payment) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Payment updated = em.merge(payment);
        em.getTransaction().commit();
        em.close();
        return updated;
    }

    public void remove(Payment payment) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(payment));
        em.getTransaction().commit();
        em.close();
    }

    public Payment findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        Payment p = em.find(Payment.class, id);
        em.close();
        return p;
    }

    public List<Payment> getAllPayments() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<Payment> list = new ArrayList<>();
        list = em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
        em.getTransaction().commit();
        em.close();
        return list;
    }

    public List<Payment> getPaymentsForMember(int memberId) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<Payment> list = new ArrayList<>();
        list = em.createQuery("SELECT p FROM Payment p WHERE p.member.id = :mid", Payment.class)
                 .setParameter("mid", memberId)
                 .getResultList();
        em.getTransaction().commit();
        em.close();
        return list;
    }
}
