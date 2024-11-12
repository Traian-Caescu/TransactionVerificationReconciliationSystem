package org.example.controller;

import org.example.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

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
}
