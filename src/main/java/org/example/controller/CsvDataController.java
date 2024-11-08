package org.example.controller;

import org.example.entity.StockData;
import org.example.service.CsvDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/data")
public class CsvDataController {

    private final CsvDataService csvDataService;

    public CsvDataController(CsvDataService csvDataService) {
        this.csvDataService = csvDataService;
    }

    @GetMapping("/load")
    public String loadData() {
        csvDataService.loadCsvData();
        return "redirect:/data/view";
    }

    @GetMapping("/view")
    public String viewStockData(Model model,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                @RequestParam(required = false, defaultValue = "date") String sortBy,
                                @RequestParam(required = false, defaultValue = "asc") String order) {

        List<StockData> stockData = csvDataService.getFilteredStockData(startDate, endDate, sortBy, order);
        model.addAttribute("stockData", stockData);
        return "stockData";
    }
}
