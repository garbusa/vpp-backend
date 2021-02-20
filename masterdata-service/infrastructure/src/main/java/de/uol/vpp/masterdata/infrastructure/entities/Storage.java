package de.uol.vpp.masterdata.infrastructure.entities;

import de.uol.vpp.masterdata.infrastructure.entities.embeddables.StoragePower;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.StorageType;
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

    @Embedded
    private StoragePower storagePower;

    @Embedded
    private StorageType storageType;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne
    @JoinColumn(name = "decentralized_power_plant_id")
    private DecentralizedPowerPlant decentralizedPowerPlant;

}
