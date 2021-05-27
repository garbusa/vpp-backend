package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.ProducerManipulationCapacityVO;
import de.uol.vpp.action.domain.valueobjects.ProducerManipulationProducerIdVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domänen-Entität für Erzeugungsmanipulationen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProducerManipulationEntity extends AbstractManipulationEntity {
    /**
     * Betroffene Erzeugungsanlage durch der die Manipulation stattfindet
     */
    private ProducerManipulationProducerIdVO producerId;
    /**
     * Gibt an, um wie viel Prozentpunkte die Leistung der Erzeugungsanlage zu- oder abnehmen soll
     */
    private ProducerManipulationCapacityVO capacity;
}
