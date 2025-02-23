package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.OptionDTO;
import org.example.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for interacting with external APIs to fetch financial data,
 * including most active options and stock data, as well as comparing stock
 * data to transaction records.
 */
@Service
public class ExternalTransactionService {

    @Value("${rapidapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = Logger.getLogger(ExternalTransactionService.class.getName());

    public ExternalTransactionService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetch the most active options based on the current user's role.
     * Only accessible by SENIOR role.
     *
     * @return List of OptionDTO containing most active options.
     */
    public List<OptionDTO> fetchMostActiveOptions() {
        String userRole = getUserRole();
        if (!"SENIOR".equals(userRole)) {
            LOGGER.log(Level.WARNING, "Access denied. {0} role is not permitted to fetch most active options.", userRole);
            return new ArrayList<>();
        }

        String url = "https://yahoo-finance15.p.rapidapi.com/api/v1/markets/options/most-active?type=STOCKS";
        List<OptionDTO> options = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com");
            headers.set("x-rapidapi-key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            for (JsonNode optionNode : root.path("body")) {
                OptionDTO option = new OptionDTO();
                option.setSymbol(optionNode.path("symbol").asText());
                option.setLastPrice(optionNode.path("lastPrice").asDouble());
                option.setPriceChange(optionNode.path("priceChange").asDouble());
                option.setOptionsTotalVolume(optionNode.path("optionsTotalVolume").asText());
                options.add(option);
            }

            LOGGER.log(Level.INFO, "Most active options fetched successfully.");
        } catch (HttpClientErrorException e) {
            LOGGER.log(Level.SEVERE, "Error fetching most active options. Status: {0}, Response: {1}",
                    new Object[]{e.getStatusCode(), e.getResponseBodyAsString()});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing response from Yahoo Finance API", e);
        }

        return options;
    }

    /**
     * Fetch stock data for a list of symbols based on the user's role.
     * Only accessible by SENIOR role.
     *
     * @param symbols List of stock symbols.
     * @return List of OptionDTO containing stock data.
     */
    public List<OptionDTO> fetchStockData(List<String> symbols) {
        String userRole = getUserRole();
        if (!"SENIOR".equals(userRole)) {
            LOGGER.log(Level.WARNING, "Access denied. {0} role is not permitted to fetch stock data.", userRole);
            return new ArrayList<>();
        }

        List<OptionDTO> stockData = new ArrayList<>();
        String symbolQuery = String.join(",", symbols);
        String url = "https://yahoo-finance15.p.rapidapi.com/api/v2/markets/tickers?symbols=" + symbolQuery;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com");
            headers.set("x-rapidapi-key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            for (JsonNode stockNode : root.path("quoteResponse").path("result")) {
                OptionDTO stock = new OptionDTO();

                // Add checks for missing data to prevent null values
                stock.setSymbol(stockNode.path("symbol").asText("N/A")); // Default to "N/A" if symbol is missing
                stock.setLastPrice(stockNode.path("regularMarketPrice").asDouble(0.0)); // Default to 0.0 if price is missing
                stock.setPriceChange(stockNode.path("regularMarketChange").asDouble(0.0)); // Default to 0.0 if price change is missing
                stock.setOptionsTotalVolume(stockNode.path("regularMarketVolume").asText("0")); // Default to "0" if volume is missing
                stock.setSymbolType(stockNode.path("symbolType").asText("N/A")); // Default to "N/A" if symbol type is missing
                stock.setSymbolName(stockNode.path("symbolName").asText("N/A")); // Default to "N/A" if symbol name is missing
                stock.setPercentChange(stockNode.path("regularMarketChangePercent").asText("N/A")); // Default to "N/A" if percent change is missing

                stockData.add(stock);
            }

            LOGGER.log(Level.INFO, "Stock data fetched for symbols: {0}", symbols);
        } catch (HttpClientErrorException e) {
            LOGGER.log(Level.SEVERE, "Error fetching stock data. Status: {0}, Response: {1}",
                    new Object[]{e.getStatusCode(), e.getResponseBodyAsString()});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing response from Yahoo Finance API", e);
        }

        return stockData;
    }


    /**
     * Compare transactions with fetched stock data to identify mismatches.
     *
     * @param transactions List of transactions to compare.
     * @return List of mismatch messages indicating discrepancies.
     */
    public List<String> compareWithStockData(List<Transaction> transactions) {
        List<String> mismatches = new ArrayList<>();
        List<String> symbols = new ArrayList<>();

        for (Transaction transaction : transactions) {
            symbols.add(transaction.getSymbol());
        }

        List<OptionDTO> stockData = fetchStockData(symbols);

        for (Transaction transaction : transactions) {
            for (OptionDTO stock : stockData) {
                if (transaction.getSymbol().equals(stock.getSymbol()) &&
                        transaction.getPrice() != stock.getLastPrice()) {
                    mismatches.add("Mismatch for symbol " + transaction.getSymbol() +
                            ": Transaction price = " + transaction.getPrice() +
                            ", Stock price = " + stock.getLastPrice());
                }
            }
        }

        LOGGER.log(Level.INFO, "Stock data comparison completed. Mismatches found: {0}", mismatches.size());
        return mismatches;
    }

    /**
     * Helper method to retrieve the role of the currently authenticated user.
     *
     * @return The role of the current user.
     */
    private String getUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("User has no roles"))
                    .getAuthority()
                    .replace("ROLE_", ""); // Remove "ROLE_" prefix
        }
        return "UNKNOWN";
    }
}
