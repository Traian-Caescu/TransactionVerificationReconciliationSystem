package org.example.service;

import com.opencsv.CSVReader;
import org.example.entity.StockData;
import org.example.repository.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvDataService {

    private final StockDataRepository stockDataRepository;

    @Autowired
    public CsvDataService(StockDataRepository stockDataRepository) {
        this.stockDataRepository = stockDataRepository;
    }

    // Load CSV data into the database with error handling and logging
    public String loadCsvData() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("BTC-USD_stock_data.csv").getInputStream()))) {
            List<StockData> stockDataList = reader.readAll().stream()
                    .skip(1) // Skip header row
                    .map(data -> new StockData(
                            LocalDate.parse(data[0]),
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]),
                            Double.parseDouble(data[3]),
                            Double.parseDouble(data[4]),
                            Double.parseDouble(data[5]),
                            Long.parseLong(data[6])
                    ))
                    .collect(Collectors.toList());

            stockDataRepository.saveAll(stockDataList);

            System.out.println("CSV data loaded successfully. Total rows: " + stockDataList.size());
            return "CSV data loaded successfully. Total rows: " + stockDataList.size();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to load CSV data: " + e.getMessage();
        }
    }

    // Retrieve filtered stock data by date range and sorting criteria
    public List<StockData> getFilteredStockData(LocalDate startDate, LocalDate endDate, String sortBy, boolean asc) {
        List<StockData> data = stockDataRepository.findByDateRange(startDate, endDate);

        Comparator<StockData> comparator = getComparator(sortBy);
        if (!asc) {
            comparator = comparator.reversed();
        }

        return data.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    // Helper method to get comparator based on sorting criteria
    private Comparator<StockData> getComparator(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "open":
                return Comparator.comparing(StockData::getOpen);
            case "high":
                return Comparator.comparing(StockData::getHigh);
            case "low":
                return Comparator.comparing(StockData::getLow);
            case "close":
                return Comparator.comparing(StockData::getClose);
            case "volume":
                return Comparator.comparing(StockData::getVolume);
            default:
                return Comparator.comparing(StockData::getDate); // Default sort by date
        }
    }
}