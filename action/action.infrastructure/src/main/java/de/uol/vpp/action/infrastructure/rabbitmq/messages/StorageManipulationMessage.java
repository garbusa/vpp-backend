package de.uol.vpp.action.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
