package co.com.camargo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    private final RestTemplate restTemplate;

    public BatchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 120000) // 120000 ms = 2 minutos
    public void executeBatchProcess() {
        String orchestratorUrl = "http://host.docker.internal/9000/orchestrate/start";
        logger.info("Iniciando proceso batch...");
        try {
            // Realizar la llamada HTTP al servicio orquestador
            String response = restTemplate.getForObject(orchestratorUrl, String.class);
            logger.info("Respuesta del orquestador: {}", response);
        } catch (Exception e) {
            logger.error("Error al llamar al orquestador: ", e);
        }
    }
}
