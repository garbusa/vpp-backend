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

@Component
@Log4j2
public class SolarRestClient {

    private final String authHeader = "Basic Y2FybHZvbm9zc2lldHpreXVuaXZlcnNpdGFldG9sZGVuYnVyZ19nYXJidXNhOmh4Y3MwNFk1V0lQdlg=";

    public List<SolarForecastDTO> getSolarForecast(ZonedDateTime dateTime, SolarEnergyDTO dto) throws SolarRestClientException {
        dto.setRatedCapacity(dto.getRatedCapacity() / 1000); //kW to MW
        String url = this.getBaseUrl(dateTime.toInstant().toString(), dto.getAlignment(), dto.getSlope(), dto.getRatedCapacity(), dto.getLatitude(), dto.getLongitude());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response
                    = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(this.createHeaders()), String.class);
            if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
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
                    throw new SolarRestClientException("json response has no data field or is no array");
                }
            } else {
                throw new SolarRestClientException("solar rest api response is unequal to 200");
            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new SolarRestClientException("solar rest client exception while executing request", e);
        }
        return null;
    }

    private String getBaseUrl(String timestamp, Double alignment, Double slope, Double ratedCapacity, Double lat, Double lon) {
        return String.format("https://carlvonossietzkyuniversitaetoldenburg_garbusa:hxcs04Y5WIPvX@api.meteomatics.com/%sP1D:PT15M/solar_power_orientation_%f_tilt_%f_installed_capacity_%f:kW/%f,%f/json",
                timestamp, alignment, slope, ratedCapacity, lat, lon);
    }

    private HttpHeaders createHeaders() {
        return new HttpHeaders() {{
            set("Authorization", SolarRestClient.this.authHeader);
        }};
    }

}
