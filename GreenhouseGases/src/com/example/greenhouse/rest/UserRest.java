package com.example.greenhouse.rest;

import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.dao.UserDAO;
import com.example.greenhouse.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//REST resource for User management and login
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRest {

	private final UserDAO userDAO = new UserDAO();

	private EntityManager em() {
		return JPAUtil.getEntityManager();
	}

	// GET all users
	@GET
	public List<User> getAll() {
		EntityManager em = em();
		try {
			return em.createQuery("SELECT u FROM User u", User.class).getResultList();
		} finally {
			em.close();
		}
	}

	// GET one user by id
	@GET
	@Path("/{id}")
	public Response getOne(@PathParam("id") int id) {
		User u = userDAO.findById(id);
		if (u == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(u).build();
	}

	// REGISTER (create) a new user
	// URL: POST /restful-services/users
	@POST
	public Response register(User incoming) {
		// Basic validation for required fields
		if (incoming.getUsername() == null || incoming.getPassword() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Username and password required\"}")
					.build();
		}

		// Check for duplicate username
		if (userDAO.findByUsername(incoming.getUsername()) != null) {
			return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"Username already exists\"}").build();
		}

		// Default role is USER if not supplied
		if (incoming.getRole() == null || incoming.getRole().isEmpty()) {
			incoming.setRole("USER");
		}

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
			e.printStackTrace();
			return Response.serverError().entity("{\"error\":\"" + e.getMessage() + "\"}").build();
		} finally {
			em.close();
		}
	}

	// LOGIN endpoint
	// URL: POST /restful-services/users/login
	@POST
	@Path("/login")
	public Response login(User incoming) {
		// Check username and password are provided
		if (incoming.getUsername() == null || incoming.getPassword() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Username and password required\"}")
					.build();
		}

		// Lookup user and compare plain-text passwords
		User dbUser = userDAO.findByUsername(incoming.getUsername());
		if (dbUser == null || !dbUser.getPassword().equals(incoming.getPassword())) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid username or password\"}")
					.build();
		}

		// Session is just returning the user object
		return Response.ok(dbUser).build();
	}

	// UPDATE an existing user
	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") int id, User incoming) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			User db = em.find(User.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			// Only update fields that were provided in the request
			if (incoming.getUsername() != null)
				db.setUsername(incoming.getUsername());
			if (incoming.getPassword() != null)
				db.setPassword(incoming.getPassword());
			if (incoming.getRole() != null)
				db.setRole(incoming.getRole());

			User merged = em.merge(db);
			tx.commit();
			return Response.ok(merged).build();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			e.printStackTrace();
			return Response.serverError().entity("{\"error\":\"" + e.getMessage() + "\"}").build();
		} finally {
			em.close();
		}
	}

	// DELETE a user by id
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") int id) {
		EntityManager em = em();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			User db = em.find(User.class, id);
			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			em.remove(db);
			tx.commit();
			return Response.noContent().build();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			e.printStackTrace();
			return Response.serverError().entity("{\"error\":\"" + e.getMessage() + "\"}").build();
		} finally {
			em.close();
		}
	}
}
