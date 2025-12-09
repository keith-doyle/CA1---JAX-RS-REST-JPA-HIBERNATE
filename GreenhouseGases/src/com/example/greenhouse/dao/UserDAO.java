package com.example.greenhouse.dao;

import com.example.greenhouse.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class UserDAO {

	public User findById(int id) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			return em.find(User.class, id);
		} finally {
			em.close();
		}
	}

	public User findByUsername(String username) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :un", User.class);
			q.setParameter("un", username);
			try {
				return q.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}
		} finally {
			em.close();
		}
	}
}
