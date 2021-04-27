package de.uol.vpp.production.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.production.infrastructure.rest.dto.OtherEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.SolarEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.WaterEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.WindEnergyDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class MasterdataRestClient {


    public boolean isActiveVpp(String vppId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/vpp/" + vppId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.has("data")) {
                    JsonNode vpp = root.get("data");
                    return vpp.has("published") && vpp.get("published").booleanValue();
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return false;
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

    public List<String> getAllDppsByVppId(String vppBusinessKey) {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/dpp/by/vpp/";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl + vppBusinessKey, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode array = (ArrayNode) root.get("data");
                    array.forEach((node) -> ids.add(node.get("decentralizedPowerPlantId").asText()));
                }

            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return ids;
    }

    public List<WaterEnergyDTO> getAllWatersByDppId(String dppBusinessKey) {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/water/by/dpp/";
            this.addWaterDTO(dppBusinessKey, waters, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return waters;
    }

    private void addWaterDTO(String dppBusinessKey, List<WaterEnergyDTO> waters, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + dppBusinessKey, String.class);
        if (response != null && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                ArrayNode array = (ArrayNode) root.get("data");
                array.forEach((jsonNode -> {
                    WaterEnergyDTO dto = new WaterEnergyDTO();
                    dto.setWaterEnergyId(jsonNode.get("waterEnergyId").asText());
                    dto.setCapacity(jsonNode.get("capacity").asDouble());
                    dto.setEfficiency(jsonNode.get("efficiency").asDouble());
                    dto.setDensity(jsonNode.get("density").asDouble());
                    dto.setGravity(jsonNode.get("gravity").asDouble());
                    dto.setHeight(jsonNode.get("height").asDouble());
                    dto.setVolumeFlow(jsonNode.get("volumeFlow").asDouble());
                    waters.add(dto);
                }));
            }

        }
    }

    public List<WaterEnergyDTO> getAllWatersByHouseholdId(String householdBusinessKey) {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/water/by/household/";
            this.addWaterDTO(householdBusinessKey, waters, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return waters;
    }

    public List<WindEnergyDTO> getAllWindsByDppId(String dppBusinessKey) {
        List<WindEnergyDTO> winds = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/wind/by/dpp/";
            addWindDTO(dppBusinessKey, winds, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return winds;
    }

    private void addWindDTO(String dppBusinessKey, List<WindEnergyDTO> winds, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + dppBusinessKey, String.class);
        if (response != null && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                ArrayNode array = (ArrayNode) root.get("data");
                array.forEach((jsonNode -> {
                    WindEnergyDTO dto = new WindEnergyDTO();
                    dto.setWindEnergyId(jsonNode.get("windEnergyId").asText());
                    dto.setLatitude(jsonNode.get("latitude").asDouble());
                    dto.setLongitude(jsonNode.get("longitude").asDouble());
                    dto.setCapacity(jsonNode.get("capacity").asDouble());
                    dto.setEfficiency(jsonNode.get("efficiency").asDouble());
                    dto.setHeight(jsonNode.get("height").asDouble());
                    dto.setRadius(jsonNode.get("radius").asDouble());
                    winds.add(dto);
                }));
            }

        }
    }

    public List<WindEnergyDTO> getAllWindsByHouseholdId(String householdBusinessKey) {
        List<WindEnergyDTO> winds = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/wind/by/household/";
            addWindDTO(householdBusinessKey, winds, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return winds;
    }

    public List<SolarEnergyDTO> getAllSolarsByDppId(String dppBusinessKey) {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/solar/by/dpp/";
            addSolarDTO(dppBusinessKey, solars, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return solars;
    }

    private void addSolarDTO(String dppBusinessKey, List<SolarEnergyDTO> solars, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + dppBusinessKey, String.class);
        if (response != null && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                ArrayNode array = (ArrayNode) root.get("data");
                array.forEach((jsonNode -> {
                    SolarEnergyDTO dto = new SolarEnergyDTO();
                    dto.setSolarEnergyId(jsonNode.get("solarEnergyId").asText());
                    dto.setLatitude(jsonNode.get("latitude").asDouble());
                    dto.setLongitude(jsonNode.get("longitude").asDouble());
                    dto.setCapacity(jsonNode.get("capacity").asDouble());
                    dto.setRatedCapacity(jsonNode.get("ratedCapacity").asDouble());
                    dto.setAlignment(jsonNode.get("alignment").asDouble());
                    dto.setSlope(jsonNode.get("slope").asDouble());
                    solars.add(dto);
                }));
            }

        }
    }

    public List<OtherEnergyDTO> getAllOthersByDppId(String dppBusinessKey) {
        List<OtherEnergyDTO> others = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/other/by/dpp/";
            addOtherDTO(dppBusinessKey, others, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return others;
    }

    private void addOtherDTO(String dppOrHouseholdBusinessKey, List<OtherEnergyDTO> others, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + dppOrHouseholdBusinessKey, String.class);
        if (response != null && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                ArrayNode array = (ArrayNode) root.get("data");
                array.forEach((jsonNode -> {
                    OtherEnergyDTO dto = new OtherEnergyDTO();
                    dto.setOtherEnergyId(jsonNode.get("otherEnergyId").asText());
                    dto.setCapacity(jsonNode.get("capacity").asDouble());
                    dto.setRatedCapacity(jsonNode.get("ratedCapacity").asDouble());
                    others.add(dto);
                }));
            }

        }
    }

    public List<OtherEnergyDTO> getAllOthersByHousehold(String householdBusinessKey) {
        List<OtherEnergyDTO> others = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/other/by/household/";
            addOtherDTO(householdBusinessKey, others, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return others;
    }

    public List<SolarEnergyDTO> getAllSolarsByHouseholdId(String householdBusinessKey) {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/solar/by/household/";
            addSolarDTO(householdBusinessKey, solars, restTemplate, fooResourceUrl);
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return solars;
    }

}
