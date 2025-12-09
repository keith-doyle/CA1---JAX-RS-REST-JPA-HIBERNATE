package com.example.greenhouse.model;

import javax.persistence.*;

@Entity
public class EmissionRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String category;
	private String description;
	private String gasUnits;
	private String scenario;
	private double value;
	private int year;

	private boolean approved;
	private String approvedBy;

	@ManyToOne
	private Country country;

	public EmissionRecord() {
	}

	public EmissionRecord(String category, String gasUnits, double value, int year, String scenario) {
		this.category = category;
		this.gasUnits = gasUnits;
		this.value = value;
		this.year = year;
		this.scenario = scenario;
		this.approved = false;
	}

	public int getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGasUnits() {
		return gasUnits;
	}

	public void setGasUnits(String gasUnits) {
		this.gasUnits = gasUnits;
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
