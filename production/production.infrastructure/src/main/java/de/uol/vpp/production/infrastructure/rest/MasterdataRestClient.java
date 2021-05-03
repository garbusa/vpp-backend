package de.uol.vpp.production.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.production.infrastructure.rest.dto.OtherEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.SolarEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.WaterEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.WindEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.exceptions.MasterdataRestClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class MasterdataRestClient {


    public boolean isActiveVpp(String virtualPowerPlantId) throws MasterdataRestClientException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/vpp/" + virtualPowerPlantId;
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
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
        throw new MasterdataRestClientException("Something went wrong while requesting for vpp published status");
    }

    public List<String> getAllHouseholdsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/household/by/vpp/";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl + virtualPowerPlantId, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode array = (ArrayNode) root.get("data");
                    array.forEach((node) -> ids.add(node.get("householdId").asText()));
                }

            }
            return ids;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    public List<String> getAllDppsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/dpp/by/vpp/";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl + virtualPowerPlantId, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode array = (ArrayNode) root.get("data");
                    array.forEach((node) -> ids.add(node.get("decentralizedPowerPlantId").asText()));
                }

            }
            return ids;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    public List<WaterEnergyDTO> getAllWatersByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/water/by/dpp/";
            this.addWaterDTO(decentralizedPowerPlantId, waters, restTemplate, fooResourceUrl);
            return waters;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    private void addWaterDTO(String decentralizedPowerPlantId, List<WaterEnergyDTO> waters, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + decentralizedPowerPlantId, String.class);
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

    public List<WaterEnergyDTO> getAllWatersByHouseholdId(String householdId) throws MasterdataRestClientException {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/water/by/household/";
            this.addWaterDTO(householdId, waters, restTemplate, fooResourceUrl);
            return waters;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    public List<WindEnergyDTO> getAllWindsByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<WindEnergyDTO> winds = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/wind/by/dpp/";
            addWindDTO(decentralizedPowerPlantId, winds, restTemplate, fooResourceUrl);
            return winds;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    private void addWindDTO(String decentralizedPowerPlantId, List<WindEnergyDTO> winds, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + decentralizedPowerPlantId, String.class);
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

    public List<WindEnergyDTO> getAllWindsByHouseholdId(String householdId) throws MasterdataRestClientException {
        List<WindEnergyDTO> winds = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/wind/by/household/";
            addWindDTO(householdId, winds, restTemplate, fooResourceUrl);
            return winds;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    public List<SolarEnergyDTO> getAllSolarsByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/solar/by/dpp/";
            addSolarDTO(decentralizedPowerPlantId, solars, restTemplate, fooResourceUrl);
            return solars;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    private void addSolarDTO(String decentralizedPowerPlantId, List<SolarEnergyDTO> solars, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + decentralizedPowerPlantId, String.class);
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

    public List<OtherEnergyDTO> getAllOthersByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<OtherEnergyDTO> others = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/other/by/dpp/";
            addOtherDTO(decentralizedPowerPlantId, others, restTemplate, fooResourceUrl);
            return others;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    private void addOtherDTO(String decentralizedPowerPlantOrHouseholdId, List<OtherEnergyDTO> others, RestTemplate restTemplate, String fooResourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + decentralizedPowerPlantOrHouseholdId, String.class);
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

    public List<OtherEnergyDTO> getAllOthersByHousehold(String householdId) throws MasterdataRestClientException {
        List<OtherEnergyDTO> others = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/other/by/household/";
            addOtherDTO(householdId, others, restTemplate, fooResourceUrl);
            return others;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    public List<SolarEnergyDTO> getAllSolarsByHouseholdId(String householdId) throws MasterdataRestClientException {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8081/masterdata/api/solar/by/household/";
            addSolarDTO(householdId, solars, restTemplate, fooResourceUrl);
            return solars;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

}
