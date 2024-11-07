package org.example.controller;

import org.example.service.CsvDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
public class CsvDataController {

    private final CsvDataService csvDataService;

    public CsvDataController(CsvDataService csvDataService) {
        this.csvDataService = csvDataService;
    }

    @GetMapping("/load")
    public ResponseEntity<String> loadData() {
        List<String[]> data = csvDataService.loadCsvData();
        return ResponseEntity.ok("CSV data loaded successfully. Total rows: " + data.size());
    }
}
