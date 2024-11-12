package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for verifying transactions against internal data,
 * external sources, and Yahoo Finance, logging mismatches along the way.
 */
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

    /**
     * Verifies a list of transactions by comparing them with internal data and
     * external sources, such as Yahoo Finance. Logs mismatches when found.
     *
     * @param externalTransactions The list of transactions to verify.
     * @param source               The source of the external transactions.
     * @param userRole             The role of the user performing the verification.
     * @return A list of mismatch logs for mismatches found.
     */
    public List<MismatchLog> verifyTransactions(List<Transaction> externalTransactions, String source, String userRole) {
        List<MismatchLog> mismatches = new ArrayList<>();

        // Iterate over each external transaction and compare with internal and external data
        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = transactionRepository.findById(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();

                // Compare with internal data
                checkForMismatch(internalTransaction, externalTransaction, "price", internalTransaction.getPrice(), externalTransaction.getPrice(), source, mismatches, "Price mismatch detected");
                checkForMismatch(internalTransaction, externalTransaction, "quantity", internalTransaction.getQuantity(), externalTransaction.getQuantity(), source, mismatches, "Quantity mismatch detected");

                // Compare with Yahoo Finance data if the user has SENIOR role
                if ("SENIOR".equals(userRole)) {
                    double yahooPrice = fetchYahooFinancePrice(internalTransaction.getSymbol());
                    if (yahooPrice != -1) {
                        checkForMismatch(internalTransaction, externalTransaction, "yahoo_price", internalTransaction.getPrice(), yahooPrice, "Yahoo Finance", mismatches, "Yahoo Finance price mismatch detected");
                    }
                }
            } else {
                // No matching internal transaction, log mismatch
                mismatches.add(logMismatch(externalTransaction.getTransactionId(), "status", "missing", "new external transaction", source, "No matching internal transaction found"));
            }
        }

        // Save all mismatches at once for efficiency
        mismatchLogRepository.saveAll(mismatches);
        return mismatches;
    }

    /**
     * Fetches all mismatches from the repository.
     *
     * @return A list of all mismatch logs.
     */
    public List<MismatchLog> getAllMismatches() {
        return mismatchLogRepository.findAll();
    }

    /**
     * Generates a summary of the verification process, grouped by mismatch field.
     *
     * @return A map with mismatch field as key and count of mismatches as value.
     */
    public Map<String, Long> getVerificationSummary() {
        return mismatchLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(MismatchLog::getField, Collectors.counting()));
    }

    /**
     * Fetches all transaction mismatches.
     *
     * @return A list of all transaction mismatch logs.
     */
    public List<MismatchLog> getAllTransactionMismatches() {
        return mismatchLogRepository.findAll();  // Returns all mismatch logs for transactions
    }

    /**
     * Fetches mismatches specific to a given transaction ID.
     *
     * @param transactionId The ID of the transaction for which mismatches should be fetched.
     * @return A list of mismatches for the specified transaction.
     */
    public List<MismatchLog> getMismatchesByTransactionId(String transactionId) {
        return mismatchLogRepository.findByTransactionId(transactionId);
    }

    /**
     * Fetches the stock price from Yahoo Finance API for a given symbol.
     *
     * @param symbol The symbol of the stock.
     * @return The stock price, or -1 if the price could not be fetched.
     */
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

    /**
     * Compares internal and external values for a given field and logs a mismatch if found.
     *
     * @param internalTransaction  The internal transaction.
     * @param externalTransaction  The external transaction.
     * @param field                The field being compared.
     * @param internalValue        The value from the internal transaction.
     * @param externalValue        The value from the external transaction.
     * @param source               The source of the transaction data.
     * @param mismatches           The list of mismatches to store the log.
     * @param description          A description of the mismatch.
     */
    private void checkForMismatch(Transaction internalTransaction, Transaction externalTransaction, String field,
                                  Object internalValue, Object externalValue, String source,
                                  List<MismatchLog> mismatches, String description) {
        if (!internalValue.equals(externalValue)) {
            mismatches.add(logMismatch(internalTransaction.getTransactionId(), field, String.valueOf(internalValue),
                    String.valueOf(externalValue), source, description));
        }
    }

    /**
     * Logs a mismatch in the MismatchLog repository.
     *
     * @param transactionId The transaction ID.
     * @param field         The field in which the mismatch occurred.
     * @param internalValue The internal value.
     * @param externalValue The external value.
     * @param source        The source of the transaction data.
     * @param description   A description of the mismatch.
     * @return The logged mismatch.
     */
    private MismatchLog logMismatch(String transactionId, String field, String internalValue, String externalValue,
                                    String source, String description) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue, source, description);
        mismatchLogRepository.save(mismatchLog);
        return mismatchLog;
    }

    // Inner classes to handle the Yahoo Finance API response structure
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
