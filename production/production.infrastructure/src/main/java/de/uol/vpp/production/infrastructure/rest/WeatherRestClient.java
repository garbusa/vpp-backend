package de.uol.vpp.production.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.production.infrastructure.rest.dto.WeatherDTO;
import de.uol.vpp.production.infrastructure.rest.exceptions.WeatherRestClientException;
import lombok.extern.log4j.Log4j2;
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
 * REST-Client für die Anfrage an ein Wetterdienst für die benötigte Berechnung von Windkraftanlagen
 * https://openweathermap.org/api/one-call-api
 */
@Component
@Log4j2
public class WeatherRestClient {

    /**
     * Hole Wetterdaten für eine bestimmte Koordinate
     *
     * @param latitude  Breitengrad
     * @param longitude Längengrad
     * @return Liste von Wetterdaten, ein DTO bildet ein Zeitstempel ab
     * @throws WeatherRestClientException e
     */
    public List<WeatherDTO> getWeather(Double latitude, Double longitude) throws WeatherRestClientException {
        List<WeatherDTO> result = new ArrayList<>();

        String url = this.getBaseUrl(longitude, latitude);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response
                    = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("hourly") && root.get("hourly").isArray()) {
                    ArrayNode hourly = (ArrayNode) root.get("hourly");
                    hourly.forEach(hour -> {
                        WeatherDTO forecastDTO = setWeatherDTO(hour);
                        result.add(forecastDTO);
                    });
                }
            } else {
                throw new WeatherRestClientException("Es ist ein Fehler bei der Abfrage des Wetters geschehen");
            }
        } catch (RestClientException | JsonProcessingException e) {
            throw new WeatherRestClientException("Es ist ein Fehler bei der Abfrage des Wetters geschehen", e);
        }

        return result;
    }

    /**
     * Gibt die REST-Ressource für eine spezifische Koordinate wieder
     *
     * @param longitude Längengrad
     * @param latitude  Breitengrad
     * @return REST-Ressourcen URL
     */
    private String getBaseUrl(double longitude, double latitude) {
        return String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=minutely,daily,alerts&appid=c08f12d6278384104c9d1a15be86b1d7&units=metric",
                latitude, longitude);
    }

    /**
     * Konvertierung von JSON in ein DTO aus der REST-Anfrage
     *
     * @param current JSON Daten
     * @return Wetterdaten DTO
     */
    private WeatherDTO setWeatherDTO(JsonNode current) {
        WeatherDTO currentWeather = new WeatherDTO();
        currentWeather.setTimestamp(ZonedDateTime.ofInstant(Instant.ofEpochSecond(current.get("dt").asLong()), ZoneId.of("GMT+2")));
        currentWeather.setTemperatureCelsius(current.get("temp").asDouble());
        currentWeather.setWindSpeed(current.get("wind_speed").asDouble());
        currentWeather.setAirHumidity(current.get("humidity").asDouble());
        currentWeather.setAirPressure(current.get("pressure").asDouble());
        return currentWeather;
    }

}
