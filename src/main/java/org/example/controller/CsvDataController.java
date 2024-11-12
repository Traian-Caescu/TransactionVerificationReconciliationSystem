package org.example.controller;

import org.example.entity.StockData;
import org.example.service.CsvDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/data")
public class CsvDataController {

    private static final Logger LOGGER = Logger.getLogger(CsvDataController.class.getName());
    private final CsvDataService csvDataService;

    @Autowired
    public CsvDataController(CsvDataService csvDataService) {
        this.csvDataService = csvDataService;
    }

    @GetMapping("/load")
    public ResponseEntity<String> loadData() {
        try {
            String result = csvDataService.loadCsvData();
            LOGGER.log(Level.INFO, "CSV data loaded successfully.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load CSV data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load CSV data: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> getStockData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean asc) {
        try {
            List<StockData> stockData = csvDataService.getFilteredStockData(startDate, endDate, sortBy, asc);
            LOGGER.log(Level.INFO, "Filtered stock data retrieved successfully.");
            return ResponseEntity.ok(stockData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve stock data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve stock data: " + e.getMessage());
        }
    }
}
