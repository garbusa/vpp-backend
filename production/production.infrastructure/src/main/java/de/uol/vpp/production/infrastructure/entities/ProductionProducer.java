package de.uol.vpp.production.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
public class ProductionProducer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(nullable = false)
    private String producerId;

    @Column(nullable = false)
    private String producerType;

    @Column(nullable = false)
    private Double currentValue;

    @Column(nullable = false)
    private Double possibleValue;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "production_action_request_id", referencedColumnName = "actionRequestId"),
            @JoinColumn(name = "production_timestamp", referencedColumnName = "timestamp")
    })
    private Production production;
}
