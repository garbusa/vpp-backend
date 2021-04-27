package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessKey;

    @Column(nullable = false)
    private Double ratedPower;

    @Column(nullable = false)
    private Double capacity;

    @Column(nullable = false)
    private Double loadTimeHour;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne
    @JoinColumn(name = "decentralized_power_plant_id")
    private DecentralizedPowerPlant decentralizedPowerPlant;

}
