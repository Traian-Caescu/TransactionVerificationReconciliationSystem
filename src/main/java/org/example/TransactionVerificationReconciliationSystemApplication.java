package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Main entry point for the Transaction Verification and Reconciliation System.
 * This class boots up the Spring Boot application and configures essential beans.
 */
@SpringBootApplication
public class TransactionVerificationReconciliationSystemApplication {

    /**
     * Main method that runs the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(TransactionVerificationReconciliationSystemApplication.class, args);
    }

    /**
     * Bean configuration for Gson. This Gson bean is used for JSON conversion, with a custom date format.
     *
     * @return A Gson object configured with a custom date format.
     */
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") // Adjust date format as needed
                .create();
    }

    /**
     * Bean configuration for RestTemplate. RestTemplate is used for making HTTP requests.
     *
     * @return A RestTemplate object for making HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
