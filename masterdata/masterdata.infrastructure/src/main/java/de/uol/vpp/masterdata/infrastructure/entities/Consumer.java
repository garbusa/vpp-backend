package de.uol.vpp.masterdata.infrastructure.entities;

import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ConsumerPower;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ConsumerStatus;
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

    @Embedded
    private ConsumerStatus consumerStatus;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;


}
