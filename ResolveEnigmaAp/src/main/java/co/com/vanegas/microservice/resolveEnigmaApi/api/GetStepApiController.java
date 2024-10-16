package co.com.vanegas.microservice.resolveEnigmaApi.api;

import co.com.vanegas.microservice.resolveEnigmaApi.model.GetEnigmaStepResponse;
import co.com.vanegas.microservice.resolveEnigmaApi.model.JsonApiBodyRequest;
import co.com.vanegas.microservice.resolveEnigmaApi.model.JsonApiBodyResponseErrors;
import co.com.vanegas.microservice.resolveEnigmaApi.model.JsonApiBodyResponseSuccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-02-27T19:20:23.716-05:00[America/Bogota]")
@Controller
public class GetStepApiController implements GetStepApi {

    private static final Logger log = LoggerFactory.getLogger(GetStepApiController.class);

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public GetStepApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Operation(summary = "Get one enigma step API", description = "Get one enigma step API", responses = {
            @ApiResponse(responseCode = "200", description = "Search results matching criteria",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JsonApiBodyResponseSuccess.class)))),
            @ApiResponse(responseCode = "424", description = "Bad input parameter",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JsonApiBodyResponseErrors.class))))
    })
    public ResponseEntity<List<JsonApiBodyResponseSuccess>> getStep(
            @Parameter(description = "Request body get enigma step", required = true) @Valid @RequestBody JsonApiBodyRequest body) {
        String accept = request.getHeader("Accept");

        // Instanciar GetEnigmaStepResponse
        GetEnigmaStepResponse getEnigmaStepResponse = new GetEnigmaStepResponse();
        getEnigmaStepResponse.answer("Open the fridge");
        getEnigmaStepResponse.header(body.getData().get(0).getHeader());


        // Instanciar JsonApiBodyResponseSuccess y agregar datos
        JsonApiBodyResponseSuccess jsonApiBodyResponseSuccess = new JsonApiBodyResponseSuccess();
        jsonApiBodyResponseSuccess.addDataItem(getEnigmaStepResponse);




        // Crear una lista de respuestas
        List<JsonApiBodyResponseSuccess> responseList = new ArrayList<>();
        responseList.add(jsonApiBodyResponseSuccess);

//        Errores
        JsonApiBodyResponseErrors jsonApiBodyResponseErrors = new JsonApiBodyResponseErrors();
        jsonApiBodyResponseErrors.getErrors();



        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
