package com.example.fitness.model;

import javax.persistence.*;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;   // e.g. "2025-10-30"
    private double amount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Payment() { }

    public Payment(String date, double amount, Member member) {
        this.date = date;
        this.amount = amount;
        this.member = member;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
}
