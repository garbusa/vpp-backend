package de.uol.vpp.masterdata.infrastructure.entities;

import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ConsumerPower;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessKey;

    @Embedded
    private ConsumerPower consumerPower;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;


}
