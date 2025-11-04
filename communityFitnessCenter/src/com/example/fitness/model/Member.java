package com.example.fitness.model;

import javax.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String membershipId;
    private String name;
    private String phoneNumber;
    private String address;
    private String fitnessGoal;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private MembershipPlan membershipPlan;

    public Member() { }

    public Member(String membershipId, String name, String phoneNumber,
                  String address, String fitnessGoal, MembershipPlan membershipPlan) {
        this.membershipId = membershipId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.fitnessGoal = fitnessGoal;
        this.membershipPlan = membershipPlan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }

    public MembershipPlan getMembershipPlan() { return membershipPlan; }
    public void setMembershipPlan(MembershipPlan membershipPlan) { this.membershipPlan = membershipPlan; }
}