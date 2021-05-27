package de.uol.vpp.load.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RabbitMQ Austauschobjekt
 * Ist Teil der ActionRequestMessage {@link ActionRequestMessage}
 */
@Data
@NoArgsConstructor
public class GridManipulationMessage implements Serializable {
    @JsonProperty("startTimestamp")
    private Long startTimestamp;
    @JsonProperty("endTimestamp")
    private Long endTimestamp;
    @JsonProperty("type")
    private String type;
    @JsonProperty("ratedPower")
    private Double ratedCapacity;
}
