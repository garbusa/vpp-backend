package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class DecentralizedPowerPlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessKey;

    @OneToMany(mappedBy = "decentralizedPowerPlant")
    private List<Producer> producers;

    @OneToMany(mappedBy = "decentralizedPowerPlant")
    private List<Storage> storages;

    @ManyToOne
    @JoinColumn(name = "virtual_power_plant_id")
    private VirtualPowerPlant virtualPowerPlant;

}
