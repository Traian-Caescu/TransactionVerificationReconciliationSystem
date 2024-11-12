package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VerificationService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;
    private final RestTemplate restTemplate;

    @Value("${yahoo.finance.api.key}")
    private String yahooFinanceApiKey;

    @Autowired
    public VerificationService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
        this.restTemplate = restTemplate;
    }

    // Verify transactions with internal data and Yahoo Finance data, logging mismatches
    public List<MismatchLog> verifyTransactions(List<Transaction> externalTransactions, String source, String userRole) {
        List<MismatchLog> mismatches = new ArrayList<>();

        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = transactionRepository.findById(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();

                // Compare with internal data
                checkForMismatch(internalTransaction, externalTransaction, "price", internalTransaction.getPrice(), externalTransaction.getPrice(), source, mismatches, "Price mismatch detected");
                checkForMismatch(internalTransaction, externalTransaction, "quantity", internalTransaction.getQuantity(), externalTransaction.getQuantity(), source, mismatches, "Quantity mismatch detected");

                // Compare with Yahoo Finance data if user has SENIOR role
                if ("SENIOR".equals(userRole)) {
                    double yahooPrice = fetchYahooFinancePrice(internalTransaction.getSymbol());
                    if (yahooPrice != -1) {
                        checkForMismatch(internalTransaction, externalTransaction, "yahoo_price", internalTransaction.getPrice(), yahooPrice, "Yahoo Finance", mismatches, "Yahoo Finance price mismatch detected");
                    }
                }
            } else {
                mismatches.add(logMismatch(externalTransaction.getTransactionId(), "status", "missing", "new external transaction", source, "No matching internal transaction found"));
            }
        }
        mismatchLogRepository.saveAll(mismatches);  // Save all mismatches at once for efficiency
        return mismatches;
    }

    // Fetch all mismatches
    public List<MismatchLog> getAllMismatches() {
        return mismatchLogRepository.findAll();
    }

    // Generate verification summary
    public Map<String, Long> getVerificationSummary() {
        return mismatchLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(MismatchLog::getField, Collectors.counting()));
    }

    // Fetch all transaction mismatches
    public List<MismatchLog> getAllTransactionMismatches() {
        return mismatchLogRepository.findAll();  // Returns all mismatch logs for transactions
    }

    // Fetch mismatches specific to a transaction ID
    public List<MismatchLog> getMismatchesByTransactionId(String transactionId) {
        return mismatchLogRepository.findByTransactionId(transactionId);
    }

    // Fetch stock price from Yahoo Finance API
    private double fetchYahooFinancePrice(String symbol) {
        try {
            String url = "https://yfapi.net/v6/finance/quote?symbols=" + symbol;
            YahooFinanceResponse response = restTemplate.getForObject(url, YahooFinanceResponse.class);
            if (response != null && !response.getQuoteResponse().getResult().isEmpty()) {
                return response.getQuoteResponse().getResult().get(0).getRegularMarketPrice();
            }
        } catch (Exception e) {
            System.err.println("Error fetching Yahoo Finance data for symbol: " + symbol);
        }
        return -1;  // Return -1 if price not found or error occurred
    }

    private void checkForMismatch(Transaction internalTransaction, Transaction externalTransaction, String field, Object internalValue, Object externalValue, String source, List<MismatchLog> mismatches, String description) {
        if (!internalValue.equals(externalValue)) {
            mismatches.add(logMismatch(internalTransaction.getTransactionId(), field, String.valueOf(internalValue), String.valueOf(externalValue), source, description));
        }
    }

    private MismatchLog logMismatch(String transactionId, String field, String internalValue, String externalValue, String source, String description) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue, source, description);
        mismatchLogRepository.save(mismatchLog);  // Save mismatch to the database
        return mismatchLog;  // Return the logged mismatch
    }

    // Inner classes to handle Yahoo Finance API response structure
    private static class YahooFinanceResponse {
        private QuoteResponse quoteResponse;
        public QuoteResponse getQuoteResponse() {
            return quoteResponse;
        }
    }

    private static class QuoteResponse {
        private List<QuoteResult> result;
        public List<QuoteResult> getResult() {
            return result;
        }
    }

    private static class QuoteResult {
        private double regularMarketPrice;
        public double getRegularMarketPrice() {
            return regularMarketPrice;
        }
    }
}
