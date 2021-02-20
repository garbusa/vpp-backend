package de.uol.vpp.masterdata.infrastructure.entities.embeddables;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class ProducerType {
    @Column(nullable = false)
    private String productType;
    @Column(nullable = false)
    private String energyType;
}