package de.uol.vpp.production.infrastructure.rabbitmq.messages;

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
public class StorageManipulationMessage implements Serializable {
    @JsonProperty("startTimestamp")
    private Long startTimestamp;
    @JsonProperty("endTimestamp")
    private Long endTimestamp;
    @JsonProperty("storageId")
    private String storageId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("hours")
    private Double hours;
    @JsonProperty("ratedPower")
    private Double ratedPower;
}
