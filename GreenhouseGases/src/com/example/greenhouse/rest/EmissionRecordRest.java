package com.example.greenhouse.rest;

import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.model.Country;
import com.example.greenhouse.model.EmissionRecord;
import com.example.greenhouse.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//REST resource for EmissionRecord CRUD and approval
@Path("/emissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmissionRecordRest {

	private EntityManager em() {
		return JPAUtil.getEntityManager();
	}

	// Helper to find a user for X-User-Id header
	private User findUser(EntityManager em, Integer userId) {
		if (userId == null) {
			return null;
		}
		return em.find(User.class, userId);
	}

	// Helper for standard 401 response
	private Response unauthorized() {
		return Response.status(Response.Status.UNAUTHORIZED)
				.entity("{\"error\":\"You must be logged in (X-User-Id header)\"}").build();
	}

	// GET all emissions (requires logged-in user)
	@GET
	public Response getAll(@HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

			List<EmissionRecord> list = em.createQuery("SELECT e FROM EmissionRecord e", EmissionRecord.class)
					.getResultList();

			return Response.ok(list).build();
		} finally {
			em.close();
		}
	}

	// GET one emission by id (requires logged-in user)
	@GET
	@Path("/{id}")
	public Response getOne(@PathParam("id") int id, @HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

			EmissionRecord rec = em.find(EmissionRecord.class, id);
			if (rec == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			return Response.ok(rec).build();
		} finally {
			em.close();
		}
	}

	// GET emissions by category (assignment "view by category" requirement)
	// Example: GET /emissions/category/1.A.3.b.
	@GET
	@Path("/category/{category}")
	public Response getByCategory(@PathParam("category") String category, @HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

			List<EmissionRecord> list = em
					.createQuery("SELECT e FROM EmissionRecord e WHERE e.category = :cat", EmissionRecord.class)
					.setParameter("cat", category).getResultList();

			return Response.ok(list).build();
		} finally {
			em.close();
		}
	}

	// CREATE a new emission record
	@POST
	public Response create(EmissionRecord incoming, @HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

			tx.begin();

			// Attach an existing Country entity if an id was sent
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

	// UPDATE an existing emission record
	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") int id, EmissionRecord incoming, @HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

			tx.begin();
			EmissionRecord db = em.find(EmissionRecord.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			// Copy updatable fields from incoming to existing entity
			db.setCategory(incoming.getCategory());
			db.setGasUnits(incoming.getGasUnits());
			db.setValue(incoming.getValue());
			db.setYear(incoming.getYear());
			db.setDescription(incoming.getDescription());
			db.setScenario(incoming.getScenario());
			db.setApproved(incoming.isApproved());
			db.setApprovedBy(incoming.getApprovedBy());

			// Update country reference if provided
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

	// DELETE an emission record by id
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") int id, @HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

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

	// APPROVE an emission and store the approving user
	// Sets approved = true and approvedBy = username from X-User-Id
	@POST
	@Path("/{id}/approve")
	public Response approve(@PathParam("id") int id, @HeaderParam("X-User-Id") Integer userId) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			User u = findUser(em, userId);
			if (u == null) {
				return unauthorized();
			}

			tx.begin();
			EmissionRecord db = em.find(EmissionRecord.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			db.setApproved(true);
			db.setApprovedBy(u.getUsername());

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
}
