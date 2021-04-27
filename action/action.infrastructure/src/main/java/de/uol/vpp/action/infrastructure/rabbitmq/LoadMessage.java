package de.uol.vpp.action.infrastructure.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LoadMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
    @JsonProperty("timestamp")
    private Long timestamp;
}
