package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SpringBootApplication
public class TransactionVerificationReconciliationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionVerificationReconciliationSystemApplication.class, args);
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") // Adjust date format as needed
                .create();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
