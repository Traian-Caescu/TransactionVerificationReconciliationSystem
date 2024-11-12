package org.example.controller;

import org.example.service.YahooFinanceService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/stocks")
public class StockInfoController {

    @Autowired
    private YahooFinanceService yahooFinanceService;

    @GetMapping("/info/{symbol}")
    public ResponseEntity<String> getStockInfo(@PathVariable String symbol) {
        try {
            JSONObject stockInfo = yahooFinanceService.getStockInfo(symbol);
            return ResponseEntity.ok(stockInfo.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving stock info.");
        }
    }

}

