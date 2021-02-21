package de.uol.vpp.masterdata.infrastructure.entities.embeddables;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class StoragePower {
    @Column(nullable = false)
    private Double ratedPower;
}
