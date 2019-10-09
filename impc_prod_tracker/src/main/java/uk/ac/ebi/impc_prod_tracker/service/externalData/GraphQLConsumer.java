package uk.ac.ebi.impc_prod_tracker.service.externalData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.impc_prod_tracker.conf.exceptions.SystemOperationFailedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GraphQLConsumer
{
    @Value("${external_service_url}")
    private String EXTERNAL_SERVICE_URL;

    private RestTemplate restTemplate;
    private static ObjectMapper mapper = new ObjectMapper();

    public GraphQLConsumer(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public String executeQuery(String query)
    {
        ResponseEntity<String> response = null;
        try
        {
            response =
                restTemplate.postForEntity(EXTERNAL_SERVICE_URL, query, String.class);
        }
        catch(Exception e)
        {
            throw new SystemOperationFailedException(e);
        }
        String result = processResponseBody(response);
        return result;
    }

    private String processResponseBody(ResponseEntity<String> response)
    {
        String result = null;
        try
        {
            JsonNode root = mapper.readTree(response.getBody());
            checkIfErrors(root);
            JsonNode data = root.path("data");
            result = data.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    private void checkIfErrors(JsonNode root)
    {
        JsonNode errors = root.path("errors");

        if (errors.size() > 0)
        {
            List<String> errorMessages = new ArrayList<>();
            for (final JsonNode objNode : errors)
            {
                JsonNode messageNode = objNode.get("message");
                errorMessages.add(messageNode.asText());
            }
            throw new SystemOperationFailedException(
                "Error consulting data from external service.", errorMessages.toString());
        }
    }
}
