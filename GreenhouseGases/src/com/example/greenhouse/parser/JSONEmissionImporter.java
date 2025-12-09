package com.example.greenhouse.parser;

import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.model.Country;
import com.example.greenhouse.model.EmissionRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JSONEmissionImporter {

	// Import emissions from a JSON file in src/main/resources/data
	public int importFromClasspathJson(String resourceName) throws Exception {

		// Load JSON file as a classpath resource
		InputStream is = getClass().getClassLoader().getResourceAsStream("data/" + resourceName);

		if (is == null) {
			throw new IllegalStateException("Could not find JSON resource data/" + resourceName);
		}

		Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

		// Parse JSON into a JSONObject
		JSONParser parser = new JSONParser();
		JSONObject root = (JSONObject) parser.parse(reader);

		// Data is in an "Emissions" array
		JSONArray emissions = (JSONArray) root.get("Emissions");
		if (emissions == null) {
			throw new IllegalStateException("JSON root has no 'Emissions' array");
		}

		int year = 0;
		String countryName = "Ireland";
		Country country;

		// Optional metadata section in the file (year, country)
		Object dataObj = root.get("data");
		if (dataObj instanceof JSONObject) {
			JSONObject data = (JSONObject) dataObj;

			// Read year from the "date" field if present
			Object dateObj = data.get("date");
			if (dateObj != null) {
				try {
					year = Integer.parseInt(String.valueOf(dateObj));
				} catch (NumberFormatException ignored) {
					// If year cannot be parsed we leave it as 0
				}
			}

			// Read country name if present, default is "Ireland"
			String fromJsonCountry = (String) data.get("country");
			if (fromJsonCountry != null && !fromJsonCountry.trim().isEmpty()) {
				countryName = fromJsonCountry.trim();
			}
		}

		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();

		int inserted = 0;

		try {
			tx.begin();

			// Find or create the Country in the same transaction
			country = findOrCreateCountry(em, countryName);

			// Loop over each emission object in the array
			for (Object obj : emissions) {
				if (!(obj instanceof JSONObject)) {
					continue;
				}

				JSONObject recJson = (JSONObject) obj;

				// Column names come from the supplied JSON structure
				String category = (String) recJson.get("Category");
				String gasUnits = (String) recJson.get("Gas Units");
				Object valueObj = recJson.get("Value");

				if (valueObj == null) {
					// Skip rows without a numeric value
					continue;
				}

				double value;
				if (valueObj instanceof Number) {
					value = ((Number) valueObj).doubleValue();
				} else {
					try {
						value = Double.parseDouble(valueObj.toString());
					} catch (NumberFormatException ex) {
						// Skip rows where value is not a number
						continue;
					}
				}

				// Only import positive values
				if (value <= 0.0) {
					continue;
				}

				// Build a new EmissionRecord entity
				EmissionRecord rec = new EmissionRecord();
				rec.setCategory(category);
				rec.setGasUnits(gasUnits);
				rec.setValue(value);
				rec.setYear(year);
				rec.setCountry(country);

				// JSON file represents actual historical data, so scenario is null
				rec.setScenario(null);

				// Fill in a human-readable description based on category code
				rec.setDescription(lookupDescription(category));

				// Newly imported data starts as not approved
				rec.setApproved(false);
				rec.setApprovedBy(null);

				em.persist(rec);
				inserted++;
			}

			tx.commit();
		} catch (RuntimeException e) {
			// Rollback if anything goes wrong during import
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}

		return inserted;
	}

	// Find a country by name or create it if it does not exist
	private Country findOrCreateCountry(EntityManager em, String countryName) {

		List<Country> results = em.createQuery("SELECT c FROM Country c WHERE c.name = :name", Country.class)
				.setParameter("name", countryName).getResultList();

		if (!results.isEmpty()) {
			return results.get(0);
		}

		// Create a new Country with a simple ISO code based on the name
		Country c = new Country();
		c.setName(countryName);

		String iso = countryName.length() >= 2 ? countryName.substring(0, 2).toUpperCase() : countryName.toUpperCase();
		c.setIsoCode(iso);

		em.persist(c);
		return c;
	}

	// Map IPCC category codes to short descriptions
	private String lookupDescription(String category) {
		if (category == null)
			return null;
		String key = category.trim();

		switch (key) {
		case "Total w.out LULUCF":
			return "Total greenhouse gas emissions excluding land use, land-use change and forestry.";
		case "1.":
			return "Energy sector – fuel combustion activities.";
		case "1.A":
		case "1.A.":
			return "Energy industries – public electricity and heat production.";
		case "1.A.1.":
			return "Public electricity and heat production.";
		case "1.A.2.":
			return "Manufacturing industries and construction.";
		case "1.A.3.":
			return "Transport.";
		case "1.A.4.":
			return "Other sectors – residential, commercial, institutional.";
		case "1.B.":
			return "Fugitive emissions from fuels.";
		case "2.":
			return "Industrial processes and product use.";
		case "3.":
			return "Agriculture, forestry and other land use (AFOLU).";
		case "4.":
			return "Waste sector – solid waste, wastewater, etc.";
		case "5.":
			return "Other sources and sinks.";
		default:
			// Sub-codes like 1.A.3.b. keep null description
			return null;
		}
	}
}