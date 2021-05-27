package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ManipulationStartEndTimestampVO;
import de.uol.vpp.action.domain.valueobjects.ManipulationTypeVO;
import lombok.Data;

/**
 * Stellt die abstrakte Klasse für Manipulationen dar
 * Wird erweitert durch:
 * {@link ProducerManipulationEntity}
 * {@link StorageManipulationEntity}
 * {@link GridManipulationEntity}
 */
@Data
public abstract class AbstractManipulationEntity {
    /**
     * Objekte zur Identifizierung einer Manipulation, bestehend aus:
     * - Identifizierung der Maßnahmenabfrage
     * - Start und Endzeitpunkt der Manipulation
     * - Art der Manipulation
     */
    private ActionRequestIdVO actionRequestId;
    private ManipulationStartEndTimestampVO startEndTimestamp;
    private ManipulationTypeVO type;
}
