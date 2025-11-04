package com.example.fitness.dao;

import java.util.List;
import javax.persistence.EntityManager;

import com.example.fitness.model.Member;

public class MemberDAO {
	
	
    public void persist(Member member) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(member);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Member merge(Member member) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Member updated = em.merge(member);
            em.getTransaction().commit();
            return updated;
        } finally {
            em.close();
        }
    }

    public void remove(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Member managed = em.find(Member.class, id);
            if (managed != null) em.remove(managed);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Member findById(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Member.class, id);
        } finally {
            em.close();
        }
    }

    public List<Member> getAllMembers() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Member m", Member.class).getResultList();
        } finally {
            em.close();
        }
    }
}
