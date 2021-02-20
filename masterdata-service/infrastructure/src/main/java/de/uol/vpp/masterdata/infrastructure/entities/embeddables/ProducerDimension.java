package de.uol.vpp.masterdata.infrastructure.entities.embeddables;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class ProducerDimension {
    @Column(nullable = false)
    private Double height;
    @Column(nullable = false)
    private Double width;
    @Column(nullable = false)
    private Double length;
    @Column(nullable = false)
    private Double weight;
}
