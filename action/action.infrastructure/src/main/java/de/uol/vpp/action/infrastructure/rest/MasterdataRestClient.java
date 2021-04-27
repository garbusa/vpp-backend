package de.uol.vpp.action.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uol.vpp.action.infrastructure.rest.dto.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class MasterdataRestClient {

    public VirtualPowerPlantDTO getVirtualPowerPlantById(String virtualPowerPlantId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/vpp/" + virtualPowerPlantId;
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
                    dto.setOverflowThreshold(data.get("overflowThreshold").asDouble());
                    dto.setShortageThreshold(data.get("shortageThreshold").asDouble());
                    return dto;
                }
            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }

        return null;
    }


    public List<HouseholdDTO> getAllHouseholdsByVppId(String virtualPowerPlantId) {
        List<HouseholdDTO> households = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/household/by/vpp/" + virtualPowerPlantId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    root.get("data").forEach((householdJson) -> {
                        try {
                            households.add(mapper.treeToValue(householdJson, HouseholdDTO.class));
                        } catch (JsonProcessingException e) {
                            log.error(e);
                        }
                    });
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }

        return households;
    }


    public List<DecentralizedPowerPlantDTO> getAllDppsByVppId(String virtualPowerPlantId) {
        List<DecentralizedPowerPlantDTO> dpps = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/dpp/by/vpp/" + virtualPowerPlantId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    root.get("data").forEach((dppJson) -> {
                        try {
                            dpps.add(mapper.treeToValue(dppJson, DecentralizedPowerPlantDTO.class));
                        } catch (JsonProcessingException e) {
                            log.error(e);
                        }
                    });
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }

        return dpps;
    }

    public boolean updateSolarEnergy(String solarEnergyBusinessKey, String vppBusinessKey, SolarEnergyDTO dto) {
        try {
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/solar/" + solarEnergyBusinessKey + "?virtualPowerPlantId=" + vppBusinessKey;
            return this.updateRequest(fooResourceUrl, dto);
        } catch (JsonProcessingException e) {
            log.info(e);
            return false;
        }
    }

    private boolean updateRequest(String fooResourceUrl, Object dto) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.postForEntity(fooResourceUrl, dto, String.class);
        if (response != null && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.has("success") && root.get("success").booleanValue();
        }
        return false;
    }

    public boolean updateWindEnergy(String windEnergyBusinessKey, String vppBusinessKey, WindEnergyDTO dto) {
        try {
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/wind/" + windEnergyBusinessKey + "?virtualPowerPlantId=" + vppBusinessKey;
            return this.updateRequest(fooResourceUrl, dto);
        } catch (JsonProcessingException e) {
            log.info(e);
            return false;
        }
    }

    public boolean updateWaterEnergy(String waterEnergyBusinessKey, String vppBusinessKey, WaterEnergyDTO dto) {
        try {
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/water/" + waterEnergyBusinessKey + "?virtualPowerPlantId=" + vppBusinessKey;
            return this.updateRequest(fooResourceUrl, dto);
        } catch (JsonProcessingException e) {
            log.info(e);
            return false;
        }
    }

    public boolean updateStorage(String storageBusinessKey, String vppBusinessKey, StorageDTO dto) {
        try {
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/storage/" + storageBusinessKey + "?virtualPowerPlantId=" + vppBusinessKey;
            return this.updateRequest(fooResourceUrl, dto);
        } catch (JsonProcessingException e) {
            log.info(e);
            return false;
        }
    }

}
