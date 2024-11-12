package org.example.controller;

import org.example.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller handling statistical data for transactions and mismatches.
 * Provides endpoints for retrieving various statistics used in the system.
 */
@Controller
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Constructs the controller with the required StatisticsService.
     *
     * @param statisticsService service for managing statistics.
     */
    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Renders the statistics page view.
     *
     * @param model the model containing statistical data to be displayed.
     * @return the name of the statistics view template.
     */
    @GetMapping
    public String getStatistics(Model model) {
        model.addAttribute("transactionStats", statisticsService.getTransactionStatistics());
        model.addAttribute("seniorStats", statisticsService.getSeniorStatistics());
        model.addAttribute("totalMismatches", statisticsService.getTotalMismatches());
        model.addAttribute("mismatchByField", statisticsService.getMismatchStatisticsByField());
        model.addAttribute("allTransactionsWithMismatchCounts", statisticsService.getAllTransactionsWithMismatchCounts());
        return "statistics";
    }

    /**
     * Retrieves total transaction statistics by status.
     *
     * @return response entity with a map of transaction statuses and their counts.
     */
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Long>> getTransactionStatistics() {
        return ResponseEntity.ok(statisticsService.getTransactionStatistics());
    }

    /**
     * Retrieves statistics for senior transactions.
     *
     * @return response entity with a map of transaction statuses specifically for senior users.
     */
    @GetMapping("/senior")
    public ResponseEntity<Map<String, Long>> getSeniorStatistics() {
        return ResponseEntity.ok(statisticsService.getSeniorStatistics());
    }

    /**
     * Retrieves the total number of mismatches across all transactions.
     *
     * @return response entity with the total mismatch count.
     */
    @GetMapping("/mismatches")
    public ResponseEntity<Long> getTotalMismatches() {
        return ResponseEntity.ok(statisticsService.getTotalMismatches());
    }

    /**
     * Retrieves mismatch statistics grouped by field.
     *
     * @return response entity with a map of fields and their corresponding mismatch counts.
     */
    @GetMapping("/mismatches-by-field")
    public ResponseEntity<Map<String, Long>> getMismatchStatisticsByField() {
        return ResponseEntity.ok(statisticsService.getMismatchStatisticsByField());
    }

    /**
     * Retrieves transaction data with mismatches for a specific transaction ID.
     *
     * @param transactionId the ID of the transaction.
     * @return response entity with transaction and mismatch details, if available.
     */
    @GetMapping("/transaction-with-mismatches/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionWithMismatches(@PathVariable String transactionId) {
        return ResponseEntity.ok(statisticsService.getTransactionWithMismatches(transactionId));
    }

    /**
     * Retrieves all transactions with mismatch counts.
     *
     * @return response entity with a list of transactions and their associated mismatch counts.
     */
    @GetMapping("/transactions-with-mismatch-counts")
    public ResponseEntity<List<Map<String, Object>>> getAllTransactionsWithMismatchCounts() {
        return ResponseEntity.ok(statisticsService.getAllTransactionsWithMismatchCounts());
    }
}
