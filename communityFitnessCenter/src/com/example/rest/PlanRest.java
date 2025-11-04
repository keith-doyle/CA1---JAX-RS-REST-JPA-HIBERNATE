package com.example.rest;

import com.example.fitness.dao.JPAUtil;
import com.example.fitness.model.MembershipPlan;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/plans")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlanRest {

    private EntityManager em() { return JPAUtil.getEntityManager(); }

  
    @GET
    public List<MembershipPlan> getAll() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT p FROM MembershipPlan p", MembershipPlan.class).getResultList();
        } finally {
            em.close();
        }
    }

   
    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") int id) {
        EntityManager em = em();
        try {
            MembershipPlan p = em.find(MembershipPlan.class, id);
            if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(p).build();
        } finally {
            em.close();
        }
    }

 
    @POST
    public Response create(MembershipPlan incoming) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(incoming);
            tx.commit();
            return Response.status(Response.Status.CREATED).entity(incoming).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, MembershipPlan incoming) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MembershipPlan db = em.find(MembershipPlan.class, id);
            if (db == null) return Response.status(Response.Status.NOT_FOUND).build();


            db.setDescription(incoming.getDescription());
            db.setTotalCost(incoming.getTotalCost());

            MembershipPlan merged = em.merge(db);
            tx.commit();
            return Response.ok(merged).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
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
            MembershipPlan db = em.find(MembershipPlan.class, id);
            if (db == null) return Response.status(Response.Status.NOT_FOUND).build();
            em.remove(db);
            tx.commit();
            return Response.noContent().build(); 
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
