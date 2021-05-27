package de.uol.vpp.production.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RabbitMQ Austauschobjekt
 * Wird in der Queue für den Maßnahmen-Service abgelegt, sobald Erzeugungsprognose erfolgreich beendet ist
 */
@Data
@NoArgsConstructor
public class ProductionMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
    @JsonProperty("timestamp")
    private Long timestamp;
}
