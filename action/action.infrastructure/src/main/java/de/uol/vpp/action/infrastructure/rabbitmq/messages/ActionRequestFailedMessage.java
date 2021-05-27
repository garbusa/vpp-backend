package de.uol.vpp.action.infrastructure.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Austauschobjekt für die RabbitMQ Queues.
 * Ist Teil der ActionRequestMe
 */
@Data
@NoArgsConstructor
public class ActionRequestFailedMessage implements Serializable {
    @JsonProperty("actionRequestId")
    private String actionRequestId;
}
