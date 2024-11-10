package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;
    private final RestTemplate restTemplate;

    @Value("${yahoo.finance.api.key}")
    private String yahooFinanceApiKey;

    // Thresholds for validation
    @Value("${transaction.price.min:10}")
    private double minPrice;

    @Value("${transaction.price.max:10000}")
    private double maxPrice;

    @Value("${transaction.quantity.min:1}")
    private int minQuantity;

    @Value("${transaction.quantity.max:1000}")
    private int maxQuantity;

    @Autowired
    public VerificationService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
        this.restTemplate = restTemplate;
    }

    // Verify transactions with both internal and Yahoo Finance data
    public List<MismatchLog> verifyTransactions(List<Transaction> externalTransactions, String source) {
        List<MismatchLog> mismatches = new ArrayList<>();

        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = transactionRepository.findByTransactionId(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();

                // Internal data comparison
                checkForMismatch(internalTransaction.getTransactionId(), "price", internalTransaction.getPrice(), externalTransaction.getPrice(), source, mismatches, "Price mismatch detected");
                checkForMismatch(internalTransaction.getTransactionId(), "quantity", internalTransaction.getQuantity(), externalTransaction.getQuantity(), source, mismatches, "Quantity mismatch detected");
                checkForMismatch(internalTransaction.getTransactionId(), "UID", internalTransaction.getUid(), externalTransaction.getUid(), source, mismatches, "UID mismatch detected");

                // Yahoo Finance data comparison
                double yahooPrice = fetchYahooFinancePrice(internalTransaction.getSymbol());
                if (yahooPrice != -1) {
                    checkForMismatch(internalTransaction.getTransactionId(), "yahoo_price", internalTransaction.getPrice(), yahooPrice, "Yahoo Finance", mismatches, "Yahoo Finance price mismatch detected");
                }
            } else {
                mismatches.add(logMismatch(externalTransaction.getTransactionId(), "status", "missing", "new external transaction", source, "No matching internal transaction found"));
            }
        }
        mismatchLogRepository.saveAll(mismatches);  // Save all mismatches at once for efficiency
        return mismatches;
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

    private void checkForMismatch(String transactionId, String field, Object internalValue, Object externalValue, String source, List<MismatchLog> mismatches, String description) {
        if (!internalValue.equals(externalValue)) {
            mismatches.add(logMismatch(transactionId, field, String.valueOf(internalValue), String.valueOf(externalValue), source, description));
        }
    }

    private MismatchLog logMismatch(String transactionId, String field, String internalValue, String externalValue, String source, String description) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue, source, description);
        mismatchLogRepository.save(mismatchLog);  // Save mismatch to the database
        return mismatchLog;  // Return the logged mismatch
    }

    // Retrieve all mismatches for post-execution analysis
    public List<MismatchLog> getAllMismatches() {
        return mismatchLogRepository.findAll();
    }

    // Pre-execution validation for fat finger errors and range checks
    public boolean validateTransactionPreExecution(Transaction transaction) {
        boolean isValid = validateTransactionRange(transaction);

        if (!isValid) {
            System.out.println("Alert: Potential input error for transaction ID " + transaction.getTransactionId() +
                    ". Check price and quantity ranges before execution.");
        }

        return isValid;
    }

    // Validate transaction data against predefined range boundaries
    public boolean validateTransactionRange(Transaction transaction) {
        return transaction.getPrice() >= minPrice && transaction.getPrice() <= maxPrice
                && transaction.getQuantity() >= minQuantity && transaction.getQuantity() <= maxQuantity;
    }

    // Fetch mismatches specific to a transaction for reporting
    public List<MismatchLog> getMismatchesByTransactionId(String transactionId) {
        return mismatchLogRepository.findByTransactionId(transactionId);
    }

    // Generate a detailed mismatch report, providing a breakdown by source
    public void generateDetailedMismatchReport() {
        List<MismatchLog> mismatches = getAllMismatches();
        System.out.println("==== Mismatch Report ====");
        mismatches.forEach(mismatch -> System.out.println(
                "Transaction ID: " + mismatch.getTransactionId() +
                        ", Field: " + mismatch.getField() +
                        ", Internal Value: " + mismatch.getInternalValue() +
                        ", External Value: " + mismatch.getExternalValue() +
                        ", Source: " + mismatch.getSource()
        ));
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
