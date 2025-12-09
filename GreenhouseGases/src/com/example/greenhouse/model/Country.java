package com.example.greenhouse.model;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "Country")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private String name;

	@Column(name = "isoCode")
	private String isoCode;

	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<EmissionRecord> emissions;

	public Country() {
	}

	public Country(String isoCode, String name) {
		this.isoCode = isoCode;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EmissionRecord> getEmissions() {
		return emissions;
	}
}
