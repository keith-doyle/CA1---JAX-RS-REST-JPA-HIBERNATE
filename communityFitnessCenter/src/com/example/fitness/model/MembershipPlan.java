package com.example.fitness.model;

import javax.persistence.*;

@Entity
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

   
    private double totalCost;

    public MembershipPlan() { }

    public MembershipPlan(String description, double totalCost) {
        this.description = description;
        this.totalCost = totalCost;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
}