package org.example.service;

import org.example.dto.OptionDTO; // Ensure OptionDTO is imported
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.List;

@Service
public class ExternalTransactionService {

    private final RestTemplate restTemplate;

    @Value("${yahoo.finance.api.key}")
    private String apiKey;

    public ExternalTransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetch most active stock options
    public List<OptionDTO> fetchMostActiveOptions() {
        String apiUrl = "https://yahoo-finance15.p.rapidapi.com/api/v1/markets/options/most-active?type=STOCKS";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com");
        headers.set("x-rapidapi-key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseWrapper> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    ResponseWrapper.class); // Ensure this class matches the API response structure

            return response.getBody() != null ? response.getBody().getBody() : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("Error fetching most active options: " + e.getMessage());
            return Collections.emptyList(); // Return an empty list on error
        }
    }


    // Define a wrapper for the response to match the API response structure
    public static class ResponseWrapper {
        private String status;
        private List<OptionDTO> body; // List of OptionDTOs

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<OptionDTO> getBody() {
            return body;
        }

        public void setBody(List<OptionDTO> body) {
            this.body = body;
        }
    }
}
