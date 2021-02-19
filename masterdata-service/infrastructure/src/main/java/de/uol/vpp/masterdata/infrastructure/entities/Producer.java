package de.uol.vpp.masterdata.infrastructure.entities;

import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerDimension;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerPower;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessKey;

    @Embedded
    private ProducerPower power;

    @Embedded
    private ProducerDimension dimension;

    @Embedded
    private ProducerType type;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne
    @JoinColumn(name = "decentralized_power_plant_id")
    private DecentralizedPowerPlant decentralizedPowerPlant;


}