package org.example.controller;

import org.example.model.StockData;
import org.example.repository.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CsvDataController {

    private final StockDataRepository stockDataRepository;

    @Autowired
    public CsvDataController(StockDataRepository stockDataRepository) {
        this.stockDataRepository = stockDataRepository;
    }

    // Endpoint to view data as JSON
    @GetMapping("/data")
    public List<StockData> getAllData() {
        return stockDataRepository.findAll();
    }

    // Endpoint to display data in HTML using Thymeleaf
    @GetMapping("/view-data")
    public String viewData(Model model) {
        List<StockData> data = stockDataRepository.findAll();
        model.addAttribute("stockData", data);
        return "data";
    }
}
