package de.uol.vpp.action.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uol.vpp.action.infrastructure.rest.dto.ProductionDTO;
import de.uol.vpp.action.infrastructure.rest.dto.ProductionProducerDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ProductionRestClient {

    public List<ProductionDTO> getAllProductionsByActionRequestId(String actionRequestId) {
        List<ProductionDTO> productions = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = "http://localhost:8083/production/api/production/" + actionRequestId;
            ResponseEntity<String> response
                    = restTemplate.getForEntity(fooResourceUrl, String.class);
            if (response != null && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("success") && root.get("success").booleanValue() && root.get("data").isArray()) {
                    ArrayNode dataArray = (ArrayNode) root.get("data");
                    dataArray.forEach((production) -> {
                        ProductionDTO dto = new ProductionDTO();
                        dto.setActionRequestId(production.get("actionRequestId").asText());
                        dto.setVirtualPowerPlantId(production.get("virtualPowerPlantId").asText());
                        dto.setStartTimestamp(production.get("startTimestamp").asLong());
                        List<ProductionProducerDTO> producers = new ArrayList<>();
                        if (production.has("producers") && production.get("producers").isArray()) {
                            production.get("producers").forEach((producer) -> {
                                ProductionProducerDTO producerDTO = new ProductionProducerDTO();
                                producerDTO.setProducerId(producer.get("producerId").asText());
                                producerDTO.setStartTimestamp(producer.get("startTimestamp").asLong());
                                producerDTO.setProducerType(producer.get("producerType").asText());
                                producerDTO.setCurrentValue(producer.get("currentValue").asDouble());
                                producerDTO.setPossibleValue(producer.get("possibleValue").asDouble());
                                producers.add(producerDTO);
                            });
                        }
                        dto.setProducers(producers);
                        productions.add(dto);
                    });
                }
            }
        } catch (JsonProcessingException e) {
            log.info(e);
        }
        return productions;
    }


}
