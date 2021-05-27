package de.uol.vpp.load.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RabbitMQ Austauschobjekt
 * Benachrichtigt die Maßnahmen-Service, wenn die Lastgenerierung erfolgreich beendet ist.
 */
@Data
@NoArgsConstructor
public class LoadMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
    @JsonProperty("timestamp")
    private Long timestamp;
}
