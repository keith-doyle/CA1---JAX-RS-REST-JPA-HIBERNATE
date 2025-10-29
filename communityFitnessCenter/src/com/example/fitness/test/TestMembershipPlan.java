package com.example.fitness.test;

import com.example.fitness.dao.MembershipPlanDAO;
import com.example.fitness.model.MembershipPlan;

import java.util.List;

public class TestMembershipPlan {
    public static void main(String[] args) {
        try {
            MembershipPlanDAO dao = new MembershipPlanDAO();

            // 1) Create and persist a plan
            MembershipPlan plan = new MembershipPlan(" Annual Gym + Pool Access", 399.99);
            dao.persist(plan);
            System.out.println(" Persisted Membership Plan successfully!");

            // 2) Retrieve all plans from DB
            List<MembershipPlan> plans = dao.getAllPlans();
            System.out.println(" Plans found in database: " + plans.size());
            for (MembershipPlan p : plans) {
                System.out.println("ID: " + p.getId() + " | " + p.getDescription() + " | " + p.getTotalCost());
            }

            // 3) Retrieve one plan by ID
            if (!plans.isEmpty()) {
                int id = plans.get(0).getId();
                MembershipPlan fetched = dao.findPlanById(id);
                System.out.println(" Found by ID " + id + ": " +
                        fetched.getDescription() + " (€" + fetched.getTotalCost() + ")");
            }

            System.out.println("Test completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}