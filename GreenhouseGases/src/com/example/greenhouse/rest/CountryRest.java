package com.example.greenhouse.rest;

import com.example.greenhouse.dao.CountryDAO;
import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.model.Country;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//REST resource for basic Country CRUD
@Path("/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountryRest {

	private final CountryDAO dao = new CountryDAO();

	// GET all countries
	@GET
	public List<Country> getAll() {
		return dao.findAll();
	}

	// GET one country by id
	@GET
	@Path("/{id}")
	public Response getOne(@PathParam("id") int id) {
		Country c = dao.findById(id);
		return (c == null) ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(c).build();
	}

	// CREATE a new country
	@POST
	public Response create(Country incoming) {
		dao.persist(incoming);
		return Response.status(Response.Status.CREATED).entity(incoming).build();
	}

	// UPDATE an existing country
	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") int id, Country incoming) {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			em.getTransaction().begin();
			Country db = em.find(Country.class, id);

			if (db == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			db.setName(incoming.getName());
			db.setIsoCode(incoming.getIsoCode());

			em.merge(db);
			em.getTransaction().commit();

			return Response.ok(db).build();

		} catch (Exception e) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
		}
	}
}
