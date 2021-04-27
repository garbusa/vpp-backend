package de.uol.vpp.action.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.action.infrastructure.rest.dto.LoadDTO;
import de.uol.vpp.action.infrastructure.rest.dto.LoadHouseholdDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class LoadRestClient {

    public List<LoadDTO> getAllLoadsByActionRequestId(String actionRequestId) {
        List<LoadDTO> loads = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8082/load/api/load/" + actionRequestId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode dataArray = (ArrayNode) root.get("data");
                    dataArray.forEach((load) -> {
                        LoadDTO dto = new LoadDTO();
                        dto.setActionRequestId(load.get("actionRequestId").asText());
                        dto.setVirtualPowerPlantId(load.get("virtualPowerPlantId").asText());
                        dto.setStartTimestamp(load.get("startTimestamp").asLong());
                        List<LoadHouseholdDTO> households = new ArrayList<>();
                        if (load.has("households") && load.get("households").isArray()) {
                            load.get("households").forEach((household) -> {
                                LoadHouseholdDTO householdDTO = new LoadHouseholdDTO();
                                householdDTO.setHouseholdId(household.get("householdId").asText());
                                householdDTO.setStartTimestamp(household.get("startTimestamp").asLong());
                                householdDTO.setHouseholdMemberAmount(household.get("householdMemberAmount").asInt());
                                householdDTO.setLoadValue(household.get("loadValue").asDouble());
                                households.add(householdDTO);
                            });
                        }
                        dto.setHouseholds(households);
                        loads.add(dto);
                    });
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return loads;
    }

}
