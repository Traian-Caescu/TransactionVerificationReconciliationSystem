package org.example.controller;

import org.example.entity.StockData;
import org.example.service.CsvDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/data")
public class CsvDataController {

    private final CsvDataService csvDataService;

    public CsvDataController(CsvDataService csvDataService) {
        this.csvDataService = csvDataService;
    }

    @GetMapping("/load")
    public ResponseEntity<String> loadData() {
        String result = csvDataService.loadCsvData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/view")
    public String viewStockData(Model model) {
        List<StockData> stockData = csvDataService.getAllStockData();
        model.addAttribute("stockData", stockData);
        return "stockData"; // Ensure there's a 'stockData.html' template in 'src/main/resources/templates'
    }
}
