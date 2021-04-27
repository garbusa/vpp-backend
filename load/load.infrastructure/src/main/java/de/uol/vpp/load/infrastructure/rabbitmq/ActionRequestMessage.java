package de.uol.vpp.load.infrastructure.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ActionRequestMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
    @JsonProperty("vppId")
    private String vppId;
}
