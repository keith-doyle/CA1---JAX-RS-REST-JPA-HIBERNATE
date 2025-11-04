package com.example.rest;



import com.example.fitness.dao.JPAUtil;
import com.example.fitness.model.Member;
import com.example.fitness.model.Payment;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentRest {

    private EntityManager em() { return JPAUtil.getEntityManager(); }


    @GET
    public List<Payment> getAll() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
        } finally { em.close(); }
    }


    @POST
    public Response create(Payment incoming, @Context UriInfo uri) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (incoming.getMember() != null && incoming.getMember().getId() != 0) {
                Member m = em.find(Member.class, incoming.getMember().getId());
                incoming.setMember(m);
            }

            em.persist(incoming);
            tx.commit();

            UriBuilder b = uri.getAbsolutePathBuilder().path(String.valueOf(incoming.getId()));
            return Response.created(b.build()).entity(incoming).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }


    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, Payment incoming) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Payment db = em.find(Payment.class, id);
            if (db == null) return Response.status(Response.Status.NOT_FOUND).build();

            if (incoming.getDate() != null) db.setDate(incoming.getDate());
            if (incoming.getAmount() > 0.0) db.setAmount(incoming.getAmount()); 

            if (incoming.getMember() != null && incoming.getMember().getId() != 0) {
                Member m = em.find(Member.class, incoming.getMember().getId());
                db.setMember(m);
            }

            Payment merged = em.merge(db);
            tx.commit();
            return Response.ok(merged).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Payment db = em.find(Payment.class, id);
            if (db == null) return Response.status(Response.Status.NOT_FOUND).build();
            em.remove(db);
            tx.commit();
            return Response.noContent().build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }
}