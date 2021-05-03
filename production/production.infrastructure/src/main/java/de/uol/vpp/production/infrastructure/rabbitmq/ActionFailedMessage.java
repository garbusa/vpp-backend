package de.uol.vpp.production.infrastructure.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ActionFailedMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
}