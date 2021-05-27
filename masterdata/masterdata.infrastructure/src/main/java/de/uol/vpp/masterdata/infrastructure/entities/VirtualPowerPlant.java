package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Datenbank-Entität der VK {@link de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate}
 */
@Entity
@Data
public class VirtualPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(unique = true, nullable = false)
    private String id;

    @OneToMany(mappedBy = "virtualPowerPlant")
    private List<Household> households;

    @OneToMany(mappedBy = "virtualPowerPlant")
    private List<DecentralizedPowerPlant> decentralizedPowerPlants;

    @Column(nullable = false)
    private boolean published;
}
