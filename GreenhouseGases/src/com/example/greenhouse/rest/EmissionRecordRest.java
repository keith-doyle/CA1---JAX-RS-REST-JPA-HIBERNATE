package com.example.greenhouse.rest;

import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.model.Country;
import com.example.greenhouse.model.EmissionRecord;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/emissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmissionRecordRest {

	private EntityManager em() {
		return JPAUtil.getEntityManager();
	}

	@GET
	public List<EmissionRecord> getAll() {
		EntityManager em = em();
		try {
			return em.createQuery("SELECT e FROM EmissionRecord e", EmissionRecord.class).getResultList();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/{id}")
	public Response getOne(@PathParam("id") int id) {
		EntityManager em = em();
		try {
			EmissionRecord rec = em.find(EmissionRecord.class, id);
			if (rec == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			return Response.ok(rec).build();
		} finally {
			em.close();
		}
	}

	@POST
	public Response create(EmissionRecord incoming) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (incoming.getCountry() != null && incoming.getCountry().getId() != 0) {
				Country c = em.find(Country.class, incoming.getCountry().getId());
				incoming.setCountry(c);
			}

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
	public Response update(@PathParam("id") int id, EmissionRecord incoming) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			EmissionRecord db = em.find(EmissionRecord.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			db.setSector(incoming.getSector());
			db.setYear(incoming.getYear());
			db.setCo2e(incoming.getCo2e());

			if (incoming.getCountry() != null && incoming.getCountry().getId() != 0) {
				Country c = em.find(Country.class, incoming.getCountry().getId());
				db.setCountry(c);
			} else {
				db.setCountry(null);
			}

			EmissionRecord merged = em.merge(db);
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
			EmissionRecord db = em.find(EmissionRecord.class, id);
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
