package org.example.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for interacting with the Yahoo Finance API to fetch stock information.
 */
@Service
public class YahooFinanceService {

    @Value("${yahoo.finance.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private static final Logger LOGGER = Logger.getLogger(YahooFinanceService.class.getName());

    /**
     * Fetches detailed stock information from Yahoo Finance API for a given symbol.
     *
     * @param symbol The stock symbol for which the information is to be fetched.
     * @return A `JSONObject` containing the stock data.
     * @throws IOException If there is an error during the API request or response processing.
     */
    public JSONObject getStockInfo(String symbol) throws IOException {
        String url = "https://yahoo-finance15.p.rapidapi.com/api/yahoo/qu/quote/" + symbol;

        // Build the request to the Yahoo Finance API
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com")
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            // Check if the response is successful
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Parse the response body into a JSONObject
            String responseData = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseData);
            LOGGER.log(Level.INFO, "Successfully fetched stock info for symbol: " + symbol);
            return jsonResponse;
        } catch (IOException e) {
            // Log any errors during the request/response process
            LOGGER.log(Level.SEVERE, "Error fetching stock info for symbol: " + symbol, e);
            throw new IOException("Error fetching stock info for symbol: " + symbol, e);
        }
    }
}
