package co.com.camargo.orchestratorwebflux.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/orchestrate")
public class OrchestrationController {

    private static final Logger logger = LoggerFactory.getLogger(OrchestrationController.class);
    private final WebClient webClient;

    public OrchestrationController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/start")
    @CircuitBreaker(name = "orchestrationService", fallbackMethod = "fallback")
    @Retry(name = "orchestrationService")
    public Mono<ResponseEntity<String>> orchestrateResponses() {
        String requestBody1 = prepareBody(1);
        String requestBody2 = prepareBody(2);
        String requestBody3 = prepareBody(3);

        Mono<String> service1 = callService(requestBody1, "http://host.docker.internal:8070/getStep", "Service 1");
        Mono<String> service2 = callService(requestBody2, "http://host.docker.internal:8080/getStep", "Service 2");
        Mono<String> service3 = callService(requestBody3, "http://host.docker.internal:8090/getStep", "Service 3");

        return Mono.zip(service1, service2, service3)
                .flatMap(tuple -> {
                    String finalResponse = String.format(
                            "{\"data\": [{\"header\": {\"id\": \"12345\", \"type\": \"TestGiraffeRefrigerator\"}, \"answer\": \"Step1: %s - Step2: %s - Step3: %s\"}]}",
                            tuple.getT1(), tuple.getT2(), tuple.getT3()
                    );
                    logger.info("Orquestación completada. Respuesta final: {}", finalResponse);

                    return callWebhook()
                            .thenReturn(ResponseEntity.ok(finalResponse));
                });
    }

    private Mono<String> callService(String requestBody, String uri, String serviceName) {
        logger.info("Llamando a {}", serviceName);

        return webClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleWebClientError)
                .bodyToMono(String.class)
                .map(this::extractAnswer)
                .doOnError(e -> logger.error("Error en {}: {}", serviceName, e.getMessage()))
                .retryWhen(reactor.util.retry.Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(retrySignal -> logger.warn("Reintentando {}: {}", serviceName, retrySignal.failure().getMessage())))
                .onErrorResume(e -> {
                    logger.error("Error final en {}: {}", serviceName, e.getMessage());
                    return Mono.just(serviceName + " unavailable");
                });
    }

    public Mono<ResponseEntity<String>> fallback(Throwable throwable) {
        logger.error("Fallback activado: {}", throwable.getMessage());
        String errorMessage = "{\"error\": \"Servicio no disponible. Intente más tarde.\"}";
        return Mono.just(new ResponseEntity<>(errorMessage, HttpStatus.SERVICE_UNAVAILABLE));
    }

    private Mono<Void> callWebhook() {
        String webhookUrl = "http://host.docker.internal:7002/webhook";
        logger.info("Llamando al webhook en {}", webhookUrl);
        return webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{}")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> logger.info("Webhook llamado exitosamente: {}", response))
                .doOnError(e -> logger.error("Error al llamar al webhook: {}", e.getMessage()))
                .then();
    }

    private Mono<? extends Throwable> handleWebClientError(ClientResponse clientResponse) {
        logger.error("Error del servicio: {}", clientResponse.statusCode());
        return Mono.error(new RuntimeException("Error del servicio: " + clientResponse.statusCode()));
    }

    private String prepareBody(int step) {
        return String.format("{ \"data\": [{ \"header\": { \"id\": \"12345\", \"type\": \"StepsGiraffeRefrigerator\" }, \"step\": \"%d\" }] }", step);
    }

    private String extractAnswer(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return jsonNode.get(0).get("data").get(0).get("answer").asText();
        } catch (Exception e) {
            logger.error("Error extrayendo respuesta: {}", e.getMessage());
            return "Error extrayendo respuesta";
        }
    }
}
