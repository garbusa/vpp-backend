package de.uol.vpp.load.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class MasterdataRestClient {


    public List<String> getAllActiveVppIds() {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/vpp";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode name = (ArrayNode) root.get("data");
                    name.forEach((node) -> {
                        if (node.has("published") && node.get("published").booleanValue()) {
                            ids.add(node.get("virtualPowerPlantId").asText());
                        }
                    });
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }

        return ids;
    }

    public List<String> getAllHouseholdsByVppId(String vppBusinessKey) {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/household/by/vpp/";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl + vppBusinessKey, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode array = (ArrayNode) root.get("data");
                    array.forEach((node) -> ids.add(node.get("householdId").asText()));
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }

        return ids;
    }

    public int getHouseholdMemberAmountById(String householdBusinessKey) {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/household/";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl + householdBusinessKey, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && !root.get("data").isArray()) {
                    JsonNode node = root.get("data");
                    return node.get("householdMemberAmount").intValue();
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }

        return 0;
    }


}
