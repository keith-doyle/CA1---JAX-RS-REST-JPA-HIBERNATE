package com.example.greenhouse.test;

import com.example.greenhouse.dao.CountryDAO;
import com.example.greenhouse.dao.EmissionRecordDAO;
import com.example.greenhouse.model.Country;
import com.example.greenhouse.model.EmissionRecord;

import java.util.List;

public class TestEmissionRecord {
    public static void main(String[] args) {

        CountryDAO countryDAO = new CountryDAO();
        EmissionRecordDAO emissionDAO = new EmissionRecordDAO();

   
        Country ireland = new Country("Ireland", "IE");
        countryDAO.persist(ireland);

   
        EmissionRecord record =
                new EmissionRecord(ireland, "Energy", 2022, 12_345.67);
        emissionDAO.persist(record);

        List<EmissionRecord> all = emissionDAO.findAll();
        System.out.println("Records: " + all.size());
        all.forEach(e ->
                System.out.println(
                        e.getId() + " " +
                        e.getCountry().getName() + " " +   
                        e.getSector() + " " +
                        e.getYear() + " " +
                        e.getCo2e()
                )
        );

        if (!all.isEmpty()) {
            int id = all.get(0).getId();
            EmissionRecord one = emissionDAO.findById(id);
            System.out.println("Found id=" + id + " country=" + one.getCountry().getName());
        }

        System.out.println("Hibernate/JPA test completed.");
    }
}
