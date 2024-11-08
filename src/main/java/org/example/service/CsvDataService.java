package org.example.service;

import org.example.entity.StockData;
import org.example.repository.StockDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CsvDataService {

    private final StockDataRepository stockDataRepository;

    public CsvDataService(StockDataRepository stockDataRepository) {
        this.stockDataRepository = stockDataRepository;
    }

    // Method to load data from CSV into database
    public String loadCsvData() {
        try {
            ClassPathResource csvFile = new ClassPathResource("BTC-USD_stock_data.csv");
            InputStream inputStream = csvFile.getInputStream();
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            List<StockData> stockDataList = reader.readAll().stream().skip(1).map(data -> new StockData(
                    LocalDate.parse(data[0]),
                    Double.parseDouble(data[1]),
                    Double.parseDouble(data[2]),
                    Double.parseDouble(data[3]),
                    Double.parseDouble(data[4]),
                    Double.parseDouble(data[5]),
                    Long.parseLong(data[6])
            )).collect(Collectors.toList());

            stockDataRepository.saveAll(stockDataList);
            reader.close();

            return "CSV data loaded successfully. Total rows: " + stockDataList.size();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to load CSV data";
        }
    }

    // Method to retrieve all stock data for display
    public List<StockData> getFilteredStockData(LocalDate startDate, LocalDate endDate, String sortBy, String order) {
        return stockDataRepository.findByDateRangeAndSort(startDate, endDate, sortBy, order.equals("asc"));
    }
}