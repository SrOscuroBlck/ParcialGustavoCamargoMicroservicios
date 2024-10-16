package co.com.camargo.orchestratorwebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class OrchestratorWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorWebfluxApplication.class, args);
    }

    // Define a WebClient bean to be used throughout the application
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
