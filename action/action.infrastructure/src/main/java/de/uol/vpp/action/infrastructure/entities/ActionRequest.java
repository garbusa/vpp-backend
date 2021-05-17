package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.StatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Data
public class ActionRequest {

    @Id
    private String actionRequestId;

    @Column(nullable = false)
    private String virtualPowerPlantId;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    @Column(nullable = false)
    private Double shortageThreshold;

    @Column(nullable = false)
    private Double overflowThreshold;

    @OneToMany(mappedBy = "actionCatalogPrimaryKey.actionRequest", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<ActionCatalog> catalogs;

    @OneToMany(mappedBy = "producerManipulationPrimaryKey.actionRequest", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<ProducerManipulation> producerManipulations;

    @OneToMany(mappedBy = "storageManipulationPrimaryKey.actionRequest", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<StorageManipulation> storageManipulations;

    @OneToMany(mappedBy = "gridManipulationPrimaryKey.actionRequest", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<GridManipulation> gridManipulations;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

}
