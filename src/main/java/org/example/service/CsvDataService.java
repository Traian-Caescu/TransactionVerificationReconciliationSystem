package org.example.service;

import com.opencsv.CSVReader;
import org.example.entity.StockData;
import org.example.repository.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
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

    public String loadCsvData() {
        try {
            var csvFile = new ClassPathResource("BTC-USD_stock_data.csv");
            var reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()));

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

    public List<StockData> getFilteredStockData(LocalDate startDate, LocalDate endDate, String sortBy, boolean asc) {
        List<StockData> data = stockDataRepository.findByDateRange(startDate, endDate);

        Comparator<StockData> comparator;
        switch (sortBy.toLowerCase()) {
            case "open": comparator = Comparator.comparing(StockData::getOpen); break;
            case "high": comparator = Comparator.comparing(StockData::getHigh); break;
            case "low": comparator = Comparator.comparing(StockData::getLow); break;
            case "close": comparator = Comparator.comparing(StockData::getClose); break;
            case "volume": comparator = Comparator.comparing(StockData::getVolume); break;
            default: comparator = Comparator.comparing(StockData::getDate);
        }

        if (!asc) {
            comparator = comparator.reversed();
        }
        return data.stream().sorted(comparator).collect(Collectors.toList());
    }
}
