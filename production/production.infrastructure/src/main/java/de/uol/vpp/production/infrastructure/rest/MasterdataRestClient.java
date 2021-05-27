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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * REST-Client für die Abfrage von Daten aus dem Daten-Service
 */
@Component
@Log4j2
public class MasterdataRestClient {

    @Value("${vpp.masterdata.api}")
    private String MASTERDATA_URL;

    /**
     * Prüft, ob angefragte VK veröffentlich ist
     *
     * @param virtualPowerPlantId Id des VK
     * @return true/false
     * @throws MasterdataRestClientException e
     */
    public boolean isActiveVpp(String virtualPowerPlantId) throws MasterdataRestClientException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/vpp/" + virtualPowerPlantId;
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

    /**
     * Holt alle Haushalt Ids des angefragten VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste der Haushalt Ids
     * @throws MasterdataRestClientException e
     */
    public List<String> getAllHouseholdsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/household/by/vpp/";
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

    /**
     * Hole alle DK Ids aus angefragtem VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste aller DK Ids
     * @throws MasterdataRestClientException e
     */
    public List<String> getAllDppsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        List<String> ids = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/dpp/by/vpp/";
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

    /**
     * Hole alle Wasserkraftwerke eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste der Wasserkraftwerke
     * @throws MasterdataRestClientException e
     */
    public List<WaterEnergyDTO> getAllWatersByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/water/by/dpp/";
            this.addWaterDTO(decentralizedPowerPlantId, waters, restTemplate, fooResourceUrl);
            return waters;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Konvertiere JSON aus {@link MasterdataRestClient#getAllWatersByDppId(String)} oder
     * * {@link MasterdataRestClient#getAllWatersByHouseholdId(String)} (String)} in ein DTOs
     *
     * @param dppOrHouseholdId Id eines DK oder eines Haushaltes
     * @param waters           leere Liste
     * @param restTemplate     REST-Anfrage
     * @param resourceUrl      REST-Resource
     * @throws JsonProcessingException e
     */
    private void addWaterDTO(String dppOrHouseholdId, List<WaterEnergyDTO> waters, RestTemplate restTemplate, String resourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(resourceUrl + dppOrHouseholdId, String.class);
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

    /**
     * Hole alle Wasserkraftwerke eines Haushaltes
     *
     * @param householdId Id des Haushaltes
     * @return Liste der Wasserkraftwerke
     * @throws MasterdataRestClientException e
     */
    public List<WaterEnergyDTO> getAllWatersByHouseholdId(String householdId) throws MasterdataRestClientException {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/water/by/household/";
            this.addWaterDTO(householdId, waters, restTemplate, fooResourceUrl);
            return waters;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Hole alle Windkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste der Windkraftanlagen
     * @throws MasterdataRestClientException e
     */
    public List<WindEnergyDTO> getAllWindsByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<WindEnergyDTO> winds = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/wind/by/dpp/";
            addWindDTO(decentralizedPowerPlantId, winds, restTemplate, fooResourceUrl);
            return winds;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Konvertiere JSON aus {@link MasterdataRestClient#getAllWindsByDppId(String)}  oder
     * * {@link MasterdataRestClient#getAllWindsByHouseholdId(String)} in ein DTOs
     *
     * @param dppOrHouseholdId Id eines DK oder eines Haushaltes
     * @param winds            leere Liste
     * @param restTemplate     REST-Anfrage
     * @param resourceUrl      REST-Resource
     * @throws JsonProcessingException e
     */
    private void addWindDTO(String dppOrHouseholdId, List<WindEnergyDTO> winds, RestTemplate restTemplate, String resourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(resourceUrl + dppOrHouseholdId, String.class);
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

    /**
     * Hole alle Windkraftanlagen eines Haushaltes
     *
     * @param householdId Id des Haushaltes
     * @return Liste der Windkraftanlagen
     * @throws MasterdataRestClientException e
     */
    public List<WindEnergyDTO> getAllWindsByHouseholdId(String householdId) throws MasterdataRestClientException {
        List<WindEnergyDTO> winds = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/wind/by/household/";
            addWindDTO(householdId, winds, restTemplate, fooResourceUrl);
            return winds;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Hole alle Solaranlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste der Solaranlagen
     * @throws MasterdataRestClientException e
     */
    public List<SolarEnergyDTO> getAllSolarsByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/solar/by/dpp/";
            addSolarDTO(decentralizedPowerPlantId, solars, restTemplate, fooResourceUrl);
            return solars;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Konvertiere JSON aus {@link MasterdataRestClient#getAllSolarsByDppId(String)} oder
     * * {@link MasterdataRestClient#getAllSolarsByHouseholdId(String)} in ein DTOs
     *
     * @param dppOrHouseholdId Id eines DK oder eines Haushaltes
     * @param solars           leere Liste
     * @param restTemplate     REST-Anfrage
     * @param resourceUrl      REST-Resource
     * @throws JsonProcessingException e
     */
    private void addSolarDTO(String dppOrHouseholdId, List<SolarEnergyDTO> solars, RestTemplate restTemplate, String resourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(resourceUrl + dppOrHouseholdId, String.class);
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

    /**
     * Hole alle alternativen Erzeugungsanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste der alternativen Erzeugungsanlagen
     * @throws MasterdataRestClientException e
     */
    public List<OtherEnergyDTO> getAllOthersByDppId(String decentralizedPowerPlantId) throws MasterdataRestClientException {
        List<OtherEnergyDTO> others = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/other/by/dpp/";
            addOtherDTO(decentralizedPowerPlantId, others, restTemplate, fooResourceUrl);
            return others;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Konvertiere JSON aus {@link MasterdataRestClient#getAllOthersByDppId(String)} oder
     * * {@link MasterdataRestClient#getAllOthersByHousehold(String)}  in ein DTOs
     *
     * @param dppOrHouseholdId Id eines DK oder eines Haushaltes
     * @param others           leere Liste
     * @param restTemplate     REST-Anfrage
     * @param resourceUrl      REST-Resource
     * @throws JsonProcessingException e
     */
    private void addOtherDTO(String dppOrHouseholdId, List<OtherEnergyDTO> others, RestTemplate restTemplate, String resourceUrl) throws JsonProcessingException {
        ResponseEntity<String> response
                = restTemplate.getForEntity(resourceUrl + dppOrHouseholdId, String.class);
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

    /**
     * Hole alle alternativen Erzeugungsanlagen eines Haushaltes
     *
     * @param householdId Id des Haushaltes
     * @return Liste der alternativen Erzeugungsanlagen
     * @throws MasterdataRestClientException e
     */
    public List<OtherEnergyDTO> getAllOthersByHousehold(String householdId) throws MasterdataRestClientException {
        List<OtherEnergyDTO> others = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/other/by/household/";
            addOtherDTO(householdId, others, restTemplate, fooResourceUrl);
            return others;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

    /**
     * Hole alle Solaranlagen eines Haushaltes
     *
     * @param householdId Id des Haushaltes
     * @return Liste der Solaranlagen
     * @throws MasterdataRestClientException e
     */
    public List<SolarEnergyDTO> getAllSolarsByHouseholdId(String householdId) throws MasterdataRestClientException {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/solar/by/household/";
            addSolarDTO(householdId, solars, restTemplate, fooResourceUrl);
            return solars;
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("masterdata rest client exception occured while executing request", e);
        }
    }

}
