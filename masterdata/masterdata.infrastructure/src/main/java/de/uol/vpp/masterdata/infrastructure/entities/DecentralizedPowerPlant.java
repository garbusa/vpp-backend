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
    private List<WindEnergy> winds;

    @OneToMany(mappedBy = "decentralizedPowerPlant")
    private List<WaterEnergy> waters;

    @OneToMany(mappedBy = "decentralizedPowerPlant")
    private List<SolarEnergy> solars;

    @OneToMany(mappedBy = "decentralizedPowerPlant")
    private List<OtherEnergy> others;


    @OneToMany(mappedBy = "decentralizedPowerPlant")
    private List<Storage> storages;

    @ManyToOne
    @JoinColumn(name = "virtual_power_plant_id")
    private VirtualPowerPlant virtualPowerPlant;

}
