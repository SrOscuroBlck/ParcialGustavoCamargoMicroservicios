package co.com.vanegas.microservice.resolveEnigmaApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "co.com.vanegas.microservice.resolveEnigmaApi", "co.com.vanegas.microservice.resolveEnigmaApi.api", "co.com.vanegas.microservice.resolveEnigmaApi.config" })
public class Swagger2SpringBoot {

    public static void main(String[] args) throws Exception {
        new SpringApplication(Swagger2SpringBoot.class).run(args);
    }
}
