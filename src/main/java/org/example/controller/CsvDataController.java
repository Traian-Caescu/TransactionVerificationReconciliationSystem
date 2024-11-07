package org.example.controller;

import org.example.service.CsvDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsvDataController {

    @Autowired
    private CsvDataService csvDataService;

    @GetMapping("/BTC-USD_stock_data.csv")
    public String loadCsv(@RequestParam String fileName) {
        csvDataService.loadDataFromCsv(fileName);
        return "Data loaded successfully!";
    }
}
