package org.example.controller;

import org.example.service.YahooFinanceService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller for handling stock information requests via the Yahoo Finance API.
 * Provides endpoints for retrieving data about specific stock symbols.
 */
@RestController
@RequestMapping("/api/stocks")
public class StockInfoController {

    private final YahooFinanceService yahooFinanceService;

    /**
     * Constructs the controller with the required YahooFinanceService.
     *
     * @param yahooFinanceService service for fetching stock information.
     */
    @Autowired
    public StockInfoController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    /**
     * Retrieves information about a specific stock symbol.
     * Only accessible to users with the SENIOR role.
     *
     * @param symbol the stock symbol to retrieve information for.
     * @return response entity with stock information in JSON format or an error message.
     */
    @GetMapping("/info/{symbol}")
    @PreAuthorize("hasRole('SENIOR')")
    public ResponseEntity<String> getStockInfo(@PathVariable String symbol) {
        try {
            JSONObject stockInfo = yahooFinanceService.getStockInfo(symbol);
            return ResponseEntity.ok(stockInfo.toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving stock info.");
        }
    }
}
