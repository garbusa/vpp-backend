package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class OtherEnergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private Double ratedCapacity;

    @Column(nullable = false)
    private Double capacity;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne
    @JoinColumn(name = "decentralized_power_plant_id")
    private DecentralizedPowerPlant decentralizedPowerPlant;
}