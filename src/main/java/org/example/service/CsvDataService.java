package org.example.service;

import com.opencsv.CSVReader;
import org.example.entity.StockData;
import org.example.repository.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
public class CsvDataService {

    @Autowired
    private StockDataRepository stockDataRepository;

    public void loadDataFromCsv(String fileName) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/" + fileName)))) {
            String[] line;
            reader.readNext(); // Skip header row

            while ((line = reader.readNext()) != null) {
                StockData stockData = new StockData();
                stockData.setDate(LocalDate.parse(line[0]));
                stockData.setOpen(Double.parseDouble(line[1]));
                stockData.setHigh(Double.parseDouble(line[2]));
                stockData.setLow(Double.parseDouble(line[3]));
                stockData.setClose(Double.parseDouble(line[4]));
                stockData.setAdjClose(Double.parseDouble(line[5]));
                stockData.setVolume(Long.parseLong(line[6]));

                stockDataRepository.save(stockData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
