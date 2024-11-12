package org.example.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.io.IOException;

@Service
public class YahooFinanceService {

    @Value("${yahoo.finance.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public JSONObject getStockInfo(String symbol) throws IOException {
        String url = "https://yahoo-finance15.p.rapidapi.com/api/yahoo/qu/quote/" + symbol;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseData = response.body().string();
            return new JSONObject(responseData);
        }
    }
}
