package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.OptionDTO;
import org.example.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalTransactionService {

    @Value("${yahoo.finance.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ExternalTransactionService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Fetch the most active options from Yahoo Finance API
    public List<OptionDTO> fetchMostActiveOptions() {
        String url = "https://yfapi.net/v6/finance/quote/marketSummary";
        List<OptionDTO> options = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(url + "?apikey=" + apiKey, String.class);
            JsonNode root = objectMapper.readTree(response);

            for (JsonNode optionNode : root.path("result")) {
                OptionDTO option = new OptionDTO();
                option.setSymbol(optionNode.path("symbol").asText());
                option.setPrice(optionNode.path("regularMarketPrice").asDouble());
                option.setChange(optionNode.path("regularMarketChange").asDouble());
                option.setVolume(optionNode.path("regularMarketVolume").asLong());
                options.add(option);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return options;
    }

    // Fetch stock data for specific symbols from Yahoo Finance API
    public List<OptionDTO> fetchStockData(List<String> symbols) {
        List<OptionDTO> stockData = new ArrayList<>();
        String symbolQuery = String.join(",", symbols);
        String url = "https://yfapi.net/v7/finance/quote?symbols=" + symbolQuery + "&apikey=" + apiKey;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            for (JsonNode stockNode : root.path("quoteResponse").path("result")) {
                OptionDTO stock = new OptionDTO();
                stock.setSymbol(stockNode.path("symbol").asText());
                stock.setPrice(stockNode.path("regularMarketPrice").asDouble());
                stock.setChange(stockNode.path("regularMarketChange").asDouble());
                stock.setVolume(stockNode.path("regularMarketVolume").asLong());
                stock.setMarketCap(stockNode.path("marketCap").asLong());
                stock.setPeRatio(stockNode.path("trailingPE").asDouble());
                stockData.add(stock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stockData;
    }

    // Compare transaction prices with stock prices from Yahoo Finance
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
                        transaction.getPrice() != stock.getPrice()) {
                    mismatches.add("Mismatch for symbol " + transaction.getSymbol() +
                            ": Transaction price = " + transaction.getPrice() +
                            ", Stock price = " + stock.getPrice());
                }
            }
        }

        return mismatches;
    }
}
