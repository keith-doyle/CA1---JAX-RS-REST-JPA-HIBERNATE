package com.example.greenhouse.rest;

import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.model.Country;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/countries")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CountryRest {

	private EntityManager em() {
		return JPAUtil.getEntityManager();
	}

	@GET
	public List<Country> getAll() {
		EntityManager em = em();
		try {
			return em.createQuery("SELECT c FROM Country c", Country.class).getResultList();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/{id}")
	public Response getOne(@PathParam("id") int id) {
		EntityManager em = em();
		try {
			Country c = em.find(Country.class, id);
			if (c == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			return Response.ok(c).build();
		} finally {
			em.close();
		}
	}

	@POST
	public Response create(Country incoming) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(incoming);
			tx.commit();

			return Response.status(Response.Status.CREATED).entity(incoming).build();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") int id, Country incoming) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Country db = em.find(Country.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			db.setIsoCode(incoming.getIsoCode());
			db.setName(incoming.getName());

			Country merged = em.merge(db);
			tx.commit();
			return Response.ok(merged).build();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") int id) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Country db = em.find(Country.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			em.remove(db);
			tx.commit();
			return Response.noContent().build();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}
}
