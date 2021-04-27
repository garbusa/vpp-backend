package de.uol.vpp.masterdata.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class VirtualPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessKey;

    @OneToMany(mappedBy = "virtualPowerPlant")
    private List<Household> households;

    @OneToMany(mappedBy = "virtualPowerPlant")
    private List<DecentralizedPowerPlant> decentralizedPowerPlants;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private Double shortageThreshold;

    @Column(nullable = false)
    private Double overflowThreshold;
}
