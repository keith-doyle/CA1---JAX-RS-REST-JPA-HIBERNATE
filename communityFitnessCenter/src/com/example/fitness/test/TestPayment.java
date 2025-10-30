package com.example.fitness.test;

import com.example.fitness.dao.MemberDAO;
import com.example.fitness.dao.PaymentDAO;
import com.example.fitness.dao.MembershipPlanDAO;
import com.example.fitness.model.Member;
import com.example.fitness.model.MembershipPlan;
import com.example.fitness.model.Payment;

import java.util.List;

public class TestPayment {
    public static void main(String[] args) {
        MembershipPlanDAO planDAO = new MembershipPlanDAO();
        MemberDAO memberDAO = new MemberDAO();
        PaymentDAO paymentDAO = new PaymentDAO();

        // Ensure a plan + member exist
        MembershipPlan plan = new MembershipPlan("Quarterly Gym", 129.99);
        planDAO.persist(plan);

        Member m = new Member("MB200", "Bob Jones", "0859998888",
                "45 Oak Lane", "Weight Loss", plan);
        memberDAO.persist(m);

        // Create payments for that member
        paymentDAO.persist(new Payment("2025-10-30", 50.00, m));
        paymentDAO.persist(new Payment("2025-11-30", 79.99, m));

        // Read back payments for that member
        List<Payment> list = paymentDAO.getPaymentsForMember(m.getId());
        System.out.println("Payments for " + m.getName() + ": " + list.size());
        for (Payment p : list) {
            System.out.println("  " + p.getDate() + " | " + p.getAmount());
        }
    }
}