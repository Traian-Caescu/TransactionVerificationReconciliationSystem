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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public List<OptionDTO> fetchMostActiveOptions() {
        String url = "https://yahoo-finance15.p.rapidapi.com/api/v1/markets/options/most-active?type=STOCKS";
        List<OptionDTO> options = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com");
            headers.set("x-rapidapi-key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            // Adjusted path to 'body' based on API response structure
            for (JsonNode optionNode : root.path("body")) {
                OptionDTO option = new OptionDTO();
                option.setSymbol(optionNode.path("symbol").asText());
                option.setLastPrice(optionNode.path("lastPrice").asDouble());
                option.setPriceChange(optionNode.path("priceChange").asDouble());
                option.setOptionsTotalVolume(optionNode.path("optionsTotalVolume").asText());
                options.add(option);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.log(Level.SEVERE, "Error fetching most active options from Yahoo Finance API. Status: " + e.getStatusCode() + ", Response: " + e.getResponseBodyAsString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing response from Yahoo Finance API", e);
        }

        return options;
    }


    public List<OptionDTO> fetchStockData(List<String> symbols) {
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
                stock.setSymbol(stockNode.path("symbol").asText());
                stock.setLastPrice(stockNode.path("regularMarketPrice").asDouble());
                stock.setPriceChange(stockNode.path("regularMarketChange").asDouble());
                stock.setOptionsTotalVolume(stockNode.path("regularMarketVolume").asText());
                stockData.add(stock);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.log(Level.SEVERE, "Error fetching stock data from Yahoo Finance API. Status: " + e.getStatusCode() + ", Response: " + e.getResponseBodyAsString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing response from Yahoo Finance API", e);
        }

        return stockData;
    }

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

        return mismatches;
    }
}
