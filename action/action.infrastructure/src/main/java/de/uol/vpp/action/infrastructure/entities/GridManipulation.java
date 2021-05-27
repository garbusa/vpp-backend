package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.ManipulationTypeEnum;
import de.uol.vpp.action.infrastructure.entities.embedded.ManipulationPrimaryKey;
import lombok.Data;

import javax.persistence.*;

/**
 * Datenbank-Entität der Stromnetzmanipulation
 */
@Entity
@Data
public class GridManipulation {

    @EmbeddedId
    private ManipulationPrimaryKey manipulationPrimaryKey;


    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ManipulationTypeEnum manipulationType;

    @Column(nullable = false)
    private Double ratedPower;

}
