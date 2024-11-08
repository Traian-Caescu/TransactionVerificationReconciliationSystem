package org.example.controller;

import org.example.entity.StockData;
import org.example.service.CsvDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/data")
public class CsvDataController {

    private final CsvDataService csvDataService;

    @Autowired
    public CsvDataController(CsvDataService csvDataService) {
        this.csvDataService = csvDataService;
    }

    // Load CSV Data into Database
    @GetMapping("/load")
    public ResponseEntity<String> loadData() {
        String result = csvDataService.loadCsvData();
        return ResponseEntity.ok(result);
    }

    // Get filtered stock data
    @GetMapping
    public ResponseEntity<List<StockData>> getStockData(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean asc) {
        List<StockData> stockData = csvDataService.getFilteredStockData(startDate, endDate, sortBy, asc);
        return ResponseEntity.ok(stockData);
    }
}
