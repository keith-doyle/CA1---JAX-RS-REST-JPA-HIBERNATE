package com.example.greenhouse.model;

import javax.persistence.*;

@Entity
public class EmissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "country_id")   
    private Country country;

    private String sector;
    private int year;
    private double co2e;

    public EmissionRecord() {}
    
   public EmissionRecord(Country country, String sector, int year, double co2e) {
        this.country = country;
        this.sector = sector;
        this.year = year;
        this.co2e = co2e;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getCo2e() { return co2e; }
    public void setCo2e(double co2e) { this.co2e = co2e; }
}
