package com.example.rest;

import com.example.fitness.dao.JPAUtil;
import com.example.fitness.model.Member;
import com.example.fitness.model.MembershipPlan;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/members")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MemberRest {

    private EntityManager em() { return JPAUtil.getEntityManager(); }


    @GET
    public List<Member> getAll() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT m FROM Member m", Member.class).getResultList();
        } finally { em.close(); }
    }


    @POST
    public Response create(Member incoming, @Context UriInfo uri) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

           
            if (incoming.getMembershipPlan() != null && incoming.getMembershipPlan().getId() != 0) {
                MembershipPlan plan = em.find(MembershipPlan.class, incoming.getMembershipPlan().getId());
                incoming.setMembershipPlan(plan);
            }

            em.persist(incoming);
            tx.commit();

            UriBuilder b = uri.getAbsolutePathBuilder().path(String.valueOf(incoming.getId()));
            return Response.created(b.build()).entity(incoming).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

  
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, Member incoming) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Member db = em.find(Member.class, id);
            if (db == null) return Response.status(Response.Status.NOT_FOUND).build();

            if (incoming.getMembershipId() != null) db.setMembershipId(incoming.getMembershipId());
            if (incoming.getName() != null) db.setName(incoming.getName());
            if (incoming.getPhoneNumber() != null) db.setPhoneNumber(incoming.getPhoneNumber());
            if (incoming.getAddress() != null) db.setAddress(incoming.getAddress());
            if (incoming.getFitnessGoal() != null) db.setFitnessGoal(incoming.getFitnessGoal());

            if (incoming.getMembershipPlan() != null && incoming.getMembershipPlan().getId() != 0) {
                MembershipPlan plan = em.find(MembershipPlan.class, incoming.getMembershipPlan().getId());
                db.setMembershipPlan(plan);
            }

            Member merged = em.merge(db);
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
            Member db = em.find(Member.class, id);
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