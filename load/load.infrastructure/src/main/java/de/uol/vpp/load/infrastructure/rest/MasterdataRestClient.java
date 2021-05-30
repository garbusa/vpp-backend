package de.uol.vpp.load.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.load.infrastructure.rest.exceptions.MasterdataRestClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * REST-Client um Daten aus dem Daten-Service zu beziehen
 */
@Component
@Log4j2
public class MasterdataRestClient {

    @Value("${vpp.masterdata.api}")
    private String MASTERDATA_URL;

    /**
     * Prüft, ob angefragtes VK veröffentlicht ist
     *
     * @param vppId Id des VK
     * @return true/false
     * @throws MasterdataRestClientException e
     */
    public boolean isActiveVpp(String vppId) throws MasterdataRestClientException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/vpp/" + vppId;
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
            throw new MasterdataRestClientException("Die Prüfung des Veröffentlichtenstatus mittels des Datenservices ist fehlgeschlagen.", e);
        }
        throw new MasterdataRestClientException("Die Prüfung des Veröffentlichtenstatus mittels des Datenservices ist fehlgeschlagen.");
    }

    /**
     * Hole alle Haushalt Ids durch Id des VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste der Haushalt Ids
     * @throws MasterdataRestClientException e
     */
    public List<String> getAllHouseholdsByVppId(String virtualPowerPlantId) throws MasterdataRestClientException {
        try {
            List<String> ids = new ArrayList<>();
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
            throw new MasterdataRestClientException("Die Anfrage der Haushalte aus dem Datenservice ist fehlgeschlagen.", e);
        }
    }

    /**
     * Hole Anzahl der Haushaltsmitglieder aus aktuellem Haushalt
     *
     * @param householdId Id des Haushalts
     * @return Anzahl der Haushaltsmitglieder
     * @throws MasterdataRestClientException e
     */
    public int getHouseholdMemberAmountById(String householdId) throws MasterdataRestClientException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = MASTERDATA_URL + "/household/";
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl + householdId, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && !root.get("data").isArray()) {
                    JsonNode node = root.get("data");
                    return node.get("householdMemberAmount").intValue();
                }

            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new MasterdataRestClientException("Die Abfrage der Haushaltsmitglieder aus dem Datenservice ist fehlgeschlagen.", e);
        }
        throw new MasterdataRestClientException("Die Abfrage der Haushaltsmitglieder aus dem Datenservice ist fehlgeschlagen.");
    }


}
