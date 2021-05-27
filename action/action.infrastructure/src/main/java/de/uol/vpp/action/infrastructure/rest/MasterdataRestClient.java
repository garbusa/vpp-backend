package de.uol.vpp.action.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uol.vpp.action.infrastructure.rest.dto.DecentralizedPowerPlantDTO;
import de.uol.vpp.action.infrastructure.rest.dto.HouseholdDTO;
import de.uol.vpp.action.infrastructure.rest.dto.VirtualPowerPlantDTO;
import de.uol.vpp.action.infrastructure.rest.exceptions.MasterdataRestClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class MasterdataRestClient {

    @Value("${vpp.masterdata.api}")
    private String MASTERDATA_URL;

    public boolean isHealthy() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/actuator/health";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                return root.has("status") && root.get("status").textValue().equals("UP");
            }
        } catch (RestClientException | JsonProcessingException e) {
            return false;
        }
        return false;
    }

    public VirtualPowerPlantDTO getVirtualPowerPlantById(String virtualPowerPlantId) throws MasterdataRestClientException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/vpp/" + virtualPowerPlantId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue()) {
                    JsonNode data = root.get("data");
                    VirtualPowerPlantDTO dto = new VirtualPowerPlantDTO();
                    dto.setVirtualPowerPlantId(data.get("virtualPowerPlantId").asText());
                    dto.setPublished(data.get("published").asBoolean());
                    return dto;
                }
            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
        return null;
    }


    public List<HouseholdDTO> getAllHouseholdsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        List<HouseholdDTO> households = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/household/by/vpp/" + virtualPowerPlantId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    for (JsonNode householdJson : root.get("data")) {
                        households.add(mapper.treeToValue(householdJson, HouseholdDTO.class));
                    }

                }

            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }

        return households;
    }


    public List<DecentralizedPowerPlantDTO> getAllDppsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        List<DecentralizedPowerPlantDTO> dpps = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/dpp/by/vpp/" + virtualPowerPlantId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    for (JsonNode dppJson : root.get("data")) {
                        dpps.add(mapper.treeToValue(dppJson, DecentralizedPowerPlantDTO.class));
                    }
                }

            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }

        return dpps;
    }


}
