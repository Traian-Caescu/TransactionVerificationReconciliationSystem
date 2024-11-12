package org.example.controller;

import org.example.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }
    @GetMapping
    public String getStatistics(Model model) {
        model.addAttribute("transactionStats", statisticsService.getTransactionStatistics());
        model.addAttribute("seniorStats", statisticsService.getSeniorStatistics());
        model.addAttribute("totalMismatches", statisticsService.getTotalMismatches());
        model.addAttribute("mismatchByField", statisticsService.getMismatchStatisticsByField());
        model.addAttribute("allTransactionsWithMismatchCounts", statisticsService.getAllTransactionsWithMismatchCounts());
        return "statistics";
    }

    // Existing statistics endpoints
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Long>> getTransactionStatistics() {
        return ResponseEntity.ok(statisticsService.getTransactionStatistics());
    }

    @GetMapping("/senior")
    public ResponseEntity<Map<String, Long>> getSeniorStatistics() {
        return ResponseEntity.ok(statisticsService.getSeniorStatistics());
    }

    @GetMapping("/mismatches")
    public ResponseEntity<Long> getTotalMismatches() {
        return ResponseEntity.ok(statisticsService.getTotalMismatches());
    }

    @GetMapping("/mismatches-by-field")
    public ResponseEntity<Map<String, Long>> getMismatchStatisticsByField() {
        return ResponseEntity.ok(statisticsService.getMismatchStatisticsByField());
    }

    // New join-based endpoints
    @GetMapping("/transaction-with-mismatches/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionWithMismatches(@PathVariable String transactionId) {
        return ResponseEntity.ok(statisticsService.getTransactionWithMismatches(transactionId));
    }

    @GetMapping("/transactions-with-mismatch-counts")
    public ResponseEntity<List<Map<String, Object>>> getAllTransactionsWithMismatchCounts() {
        return ResponseEntity.ok(statisticsService.getAllTransactionsWithMismatchCounts());
    }
}
