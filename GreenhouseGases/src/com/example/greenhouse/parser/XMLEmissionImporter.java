package com.example.greenhouse.parser;

import com.example.greenhouse.dao.JPAUtil;
import com.example.greenhouse.model.Country;
import com.example.greenhouse.model.EmissionRecord;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLEmissionImporter {

	// Path to the projections XML file inside resources
	private static final String XML_RESOURCE = "/data/GreenhouseGasProjections.xml";

	// Parse the XML projections and insert rows
	public int importXmlEmissions() throws Exception {
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		int imported = 0;

		// Load the XML file from the classpath
		try (InputStream is = getClass().getResourceAsStream(XML_RESOURCE)) {
			if (is == null) {
				throw new IllegalStateException("XML resource not found: " + XML_RESOURCE);
			}

			// Standard DOM parsing setup
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			// Each emission row is represented as a <Row> element
			NodeList rows = doc.getElementsByTagName("Row");

			tx.begin();

			// Ensure Ireland exists in the Country table
			Country ireland = getOrCreateIreland(em);

			// Loop through all <Row> nodes
			for (int i = 0; i < rows.getLength(); i++) {
				Node node = rows.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element row = (Element) node;

				// Read relevant columns from the XML
				String category = getChildText(row, "Category__1_3");
				String yearStr = getChildText(row, "Year");
				String scenario = getChildText(row, "Scenario");
				String gasUnits = getChildText(row, "Gas___Units");
				String valueStr = getChildText(row, "Value");

				// We must have at least year and value
				if (yearStr == null || valueStr == null) {
					continue;
				}

				int year;
				double value;
				try {
					year = Integer.parseInt(yearStr.trim());
					value = Double.parseDouble(valueStr.trim());
				} catch (NumberFormatException ex) {
					// Skip rows with malformed numbers
					continue;
				}

				// only 2023, scenario WEM, and positive values
				if (year != 2023) {
					continue;
				}
				if (scenario == null || !scenario.trim().equalsIgnoreCase("WEM")) {
					continue;
				}
				if (value <= 0.0) {
					continue;
				}

				// Create a new EmissionRecord for this XML row
				EmissionRecord rec = new EmissionRecord();
				rec.setCountry(ireland);
				rec.setCategory(category);
				rec.setGasUnits(gasUnits);
				rec.setYear(year);
				rec.setScenario(scenario);
				rec.setValue(value);

				// Use the same description mapping as JSON importer
				rec.setDescription(lookupDescription(category));

				// New projections are not approved initially
				rec.setApproved(false);
				rec.setApprovedBy(null);

				em.persist(rec);
				imported++;
			}

			tx.commit();
			System.out.println("Imported XML records: " + imported);
			return imported;
		} catch (Exception e) {
			// Rollback transaction if import fails
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	// Look up or create the Ireland country row (isoCode "IR")
	private Country getOrCreateIreland(EntityManager em) {
		List<Country> list = em.createQuery("SELECT c FROM Country c WHERE c.isoCode = :iso", Country.class)
				.setParameter("iso", "IR").getResultList();

		if (!list.isEmpty()) {
			return list.get(0);
		}

		Country c = new Country();
		c.setIsoCode("IR");
		c.setName("Ireland");
		em.persist(c);
		return c;
	}

	// Utility method to read <tagName>text</tagName> from a Row element
	private String getChildText(Element parent, String tagName) {
		NodeList list = parent.getElementsByTagName(tagName);
		if (list.getLength() == 0)
			return null;
		Node n = list.item(0);
		return n.getTextContent();
	}

	// Same description mapping as JSON importer, based on IPCC categories
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
			return null;
		}
	}
}
