package de.uol.vpp.production.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.production.infrastructure.rest.dto.SolarEnergyDTO;
import de.uol.vpp.production.infrastructure.rest.dto.SolarForecastDTO;
import de.uol.vpp.production.infrastructure.rest.exceptions.SolarRestClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * REST-Client für die Anfrage an ein Wetterdienst für die Berechnung der Erzeugung einer Solaranlage
 * https://www.meteomatics.com/en/api/available-parameters/power-and-energy/#solar_power
 */
@Component
@Log4j2
public class SolarRestClient {

    /**
     * API-Key für Meteomatics
     */
    private final String authHeader = "Basic Y2FybHZvbm9zc2lldHpreXVuaXZlcnNpdGFldG9sZGVuYnVyZ19nYXJidXNhOmh4Y3MwNFk1V0lQdlg=";

    /**
     * Holt Tagesprognose der Erzeugungswerte einer Solaranlage in MW
     *
     * @param dateTime aktueller Zeitstempel
     * @param dto      DTO der Solaranlage
     * @return Tagesprognose der Erzeugungswerte einer Solaranlage
     * @throws SolarRestClientException e
     */
    public List<SolarForecastDTO> getSolarForecast(ZonedDateTime dateTime, SolarEnergyDTO dto) throws SolarRestClientException {
        dto.setRatedCapacity(dto.getRatedCapacity() / 1000); //kW to MW
        String url = this.getBaseUrl(dateTime.toInstant().toString(), dto.getAlignment(), dto.getSlope(), dto.getRatedCapacity(), dto.getLatitude(), dto.getLongitude());
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Sende REST-Anfrage an Schnittstelle
            ResponseEntity<String> response
                    = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(this.createHeaders()), String.class);
            if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                // Konvertiere JSON in ein DTO
                if (root.has("data") && root.get("data").isArray() && root.get("data").has(0)) {
                    if (root.get("data").get(0).has("coordinates") && root.get("data").get(0).get("coordinates").isArray() &&
                            root.get("data").get(0).get("coordinates").get(0).has("dates") && root.get("data").get(0).get("coordinates").get(0).get("dates").isArray()) {
                        ArrayNode data = (ArrayNode) root.get("data").get(0).get("coordinates").get(0).get("dates");
                        List<SolarForecastDTO> result = new ArrayList<>();
                        data.forEach((forecast) -> {
                            SolarForecastDTO forecastDTO = new SolarForecastDTO();
                            forecastDTO.setTimestamp(
                                    ZonedDateTime.ofInstant(Instant.parse(forecast.get("date").asText()), ZoneId.of("GMT+2"))
                            );
                            forecastDTO.setValue(forecast.get("value").asDouble());
                            result.add(forecastDTO);
                        });
                        return result;
                    }
                } else {
                    throw new SolarRestClientException(
                            String.format("Die Solaranlage %s konnte nicht prognostiziert werden", dto.getSolarEnergyId())
                    );
                }
            } else {
                throw new SolarRestClientException(
                        String.format("Die Solaranlage %s konnte nicht prognostiziert werden", dto.getSolarEnergyId())
                );
            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new SolarRestClientException(
                    String.format("Die Solaranlage %s konnte nicht prognostiziert werden", dto.getSolarEnergyId())
            );
        }
        return null;
    }

    /**
     * Gibt Meteomatics REST-Anfrage URL wieder
     *
     * @param timestamp     aktueller Zeitstempel
     * @param alignment     Ausrichtung in Grad Celsius
     * @param slope         Neigung in Grad Celsius
     * @param ratedCapacity Nennleistung in kWp
     * @param lat           Latitude/Breitengrad
     * @param lon           Longitude/Längengrad
     * @return REST-Resource URL
     */
    private String getBaseUrl(String timestamp, Double alignment, Double slope, Double ratedCapacity, Double lat, Double lon) {
        return String.format("https://carlvonossietzkyuniversitaetoldenburg_garbusa:hxcs04Y5WIPvX@api.meteomatics.com/%sP1D:PT15M/solar_power_orientation_%f_tilt_%f_installed_capacity_%f:kW/%f,%f/json",
                timestamp, alignment, slope, ratedCapacity, lat, lon);
    }

    /**
     * Erstellt Header mit API-Key
     *
     * @return Http Header
     */
    private HttpHeaders createHeaders() {
        return new HttpHeaders() {{
            set("Authorization", SolarRestClient.this.authHeader);
        }};
    }

}
