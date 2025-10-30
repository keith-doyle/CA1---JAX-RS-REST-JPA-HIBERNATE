package com.example.fitness.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.example.fitness.model.Member;

public class MemberDAO {

    protected static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("CommunityFitnessCenterPU");

    public MemberDAO() { }

    public void persist(Member member) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(member);
        em.getTransaction().commit();
        em.close();
    }

    public Member merge(Member member) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Member updated = em.merge(member);
        em.getTransaction().commit();
        em.close();
        return updated;
    }

    public void remove(Member member) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(member));
        em.getTransaction().commit();
        em.close();
    }

    public Member findById(int id) {
        EntityManager em = emf.createEntityManager();
        Member member = em.find(Member.class, id);
        em.close();
        return member;
    }

    public List<Member> getAllMembers() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Member> members = new ArrayList<Member>();
        members = em.createQuery("SELECT m FROM Member m", Member.class).getResultList();
        em.getTransaction().commit();
        em.close();
        return members;
    }
}
