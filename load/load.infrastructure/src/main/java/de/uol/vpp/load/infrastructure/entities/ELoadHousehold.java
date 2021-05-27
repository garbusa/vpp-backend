package de.uol.vpp.load.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Datenbank-Entität der Haushaltslast {@link de.uol.vpp.load.domain.entities.LoadHouseholdEntity}
 */
@Entity
@Data
public class ELoadHousehold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(nullable = false)
    private String householdId;

    @Column(nullable = false)
    private Integer householdMemberAmount;

    @Column(nullable = false)
    private Double householdLoad;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "eload_action_request_id", referencedColumnName = "actionRequestId"),
            @JoinColumn(name = "eload_timestamp", referencedColumnName = "timestamp")
    })
    private ELoad load;
}
