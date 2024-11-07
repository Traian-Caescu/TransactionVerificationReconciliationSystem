package org.example.service;

import com.opencsv.CSVReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class CsvDataService {

    public List<String[]> loadCsvData() {
        try {
            // Access the CSV file from the resources directory
            ClassPathResource csvFile = new ClassPathResource("BTC-USD_stock_data.csv");
            InputStream inputStream = csvFile.getInputStream();
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            // Read all data from CSV
            List<String[]> allData = reader.readAll();
            reader.close();

            return allData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load CSV data", e);
        }
    }
}
