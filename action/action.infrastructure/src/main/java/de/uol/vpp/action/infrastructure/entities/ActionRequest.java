package de.uol.vpp.action.infrastructure.entities;

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

    @OneToMany(mappedBy = "actionCatalogPrimaryKey.actionRequest", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<ActionCatalog> catalogs;

    @Column(nullable = false)
    private Boolean finished;

}
