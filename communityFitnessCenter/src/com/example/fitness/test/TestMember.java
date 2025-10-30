package com.example.fitness.test;

import com.example.fitness.dao.MemberDAO;
import com.example.fitness.dao.MembershipPlanDAO;
import com.example.fitness.model.Member;
import com.example.fitness.model.MembershipPlan;

import java.util.List;

public class TestMember {
    public static void main(String[] args) {
        MembershipPlanDAO planDAO = new MembershipPlanDAO();
        MemberDAO memberDAO = new MemberDAO();

        // Ensure there’s a plan to link
        MembershipPlan plan = new MembershipPlan("Monthly Gym Plan", 49.99);
        planDAO.persist(plan);

        // Create a new member linked to that plan
        Member member = new Member(
                "MB001",
                "Alice Smith",
                "0851234567",
                "12 Park Avenue",
                "Strength Training",
                plan
        );

        memberDAO.persist(member);
        System.out.println(" Member saved successfully!");

        // List all members
        List<Member> members = memberDAO.getAllMembers();
        System.out.println("Total Members in DB: " + members.size());
        for (Member m : members) {
            System.out.println(m.getId() + " | " + m.getName() + " | Plan: " + m.getMembershipPlan().getDescription());
        }

        // Find a specific member
        Member fetched = memberDAO.findById(member.getId());
        System.out.println("Fetched Member: " + fetched.getName() + " (Goal: " + fetched.getFitnessGoal() + ")");
    }
}