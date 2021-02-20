package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessKey;

    @OneToMany(mappedBy = "household")
    private List<Producer> producers;

    @OneToMany(mappedBy = "household")
    private List<Storage> storages;

    @OneToMany(mappedBy = "household")
    private List<Consumer> consumers;

    @Column(nullable = false)
    private Integer memberAmount;

    @ManyToOne
    @JoinColumn(name = "virtual_power_plant_id", nullable = false)
    private VirtualPowerPlant virtualPowerPlant;


}
