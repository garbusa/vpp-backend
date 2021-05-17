package de.uol.vpp.action.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class ActionRequestMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
    @JsonProperty("vppId")
    private String vppId;
    @JsonProperty("shortageThreshold")
    private Double shortageThreshold;
    @JsonProperty("overflowThreshold")
    private Double overflowThreshold;
    @JsonProperty("producerManipulations")
    private List<ProducerManipulationMessage> producerManipulations;
    @JsonProperty("storageManipulations")
    private List<StorageManipulationMessage> storageManipulations;
    @JsonProperty("gridManipulations")
    private List<GridManipulationMessage> gridManipulations;
}
