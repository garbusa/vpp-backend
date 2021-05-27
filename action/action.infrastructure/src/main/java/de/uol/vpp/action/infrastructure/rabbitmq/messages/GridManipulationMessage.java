package de.uol.vpp.action.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Austauschobjekt für die RabbitMQ Queues.
 * Diese Message ist Teil der {@link ActionRequestMessage}
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
