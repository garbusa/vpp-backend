package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;

/**
 * Datenbank-Entität der Solaranlagen {@link de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity}
 */
@Entity
@Data
public class SolarEnergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double ratedCapacity;

    @Column(nullable = false)
    private Double capacity;

    @Column(nullable = false)
    private Double alignment;

    @Column(nullable = false)
    private Double slope;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne
    @JoinColumn(name = "decentralized_power_plant_id")
    private DecentralizedPowerPlant decentralizedPowerPlant;
}