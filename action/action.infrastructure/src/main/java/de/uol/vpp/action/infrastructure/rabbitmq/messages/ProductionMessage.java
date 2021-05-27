package de.uol.vpp.action.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Austauschobjekt für die RabbitMQ Queues.
 * Diese Message wird empfangen wenn die Erzeugungswerte prognostiziert wurden
 */
@Data
@NoArgsConstructor
public class ProductionMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
    @JsonProperty("timestamp")
    private Long timestamp;
}
