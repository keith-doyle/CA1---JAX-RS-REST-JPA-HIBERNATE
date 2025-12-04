package com.example.greenhouse.test;

import com.example.greenhouse.dao.EmissionRecordDAO;
import com.example.greenhouse.model.EmissionRecord;

import java.util.List;

public class TestEmissionRecord {
    public static void main(String[] args) {
        EmissionRecordDAO dao = new EmissionRecordDAO();

        // Create & persist
        dao.persist(new EmissionRecord("Ireland", "Energy", 2022, 12_345.67));

        // Read all
        List<EmissionRecord> all = dao.findAll();
        System.out.println("Records: " + all.size());
        all.forEach(e ->
            System.out.println(e.getId() + " " + e.getCountry() + " " + e.getSector() +
                               " " + e.getYear() + " " + e.getCo2e())
        );

        // Find one
        if (!all.isEmpty()) {
            int id = all.get(0).getId();
            EmissionRecord one = dao.findById(id);
            System.out.println("Found id=" + id + " country=" + one.getCountry());
        }

        System.out.println("Hibernate/JPA test completed.");
    }
}

