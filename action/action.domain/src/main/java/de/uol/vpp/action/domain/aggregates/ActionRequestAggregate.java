package de.uol.vpp.action.domain.aggregates;

import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.entities.GridManipulationEntity;
import de.uol.vpp.action.domain.entities.ProducerManipulationEntity;
import de.uol.vpp.action.domain.entities.StorageManipulationEntity;
import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

import java.util.List;

/**
 * Domänen Aggregate des Maßnahmen-Services.
 * Das Aggregate beinhaltet Informationen wie die Manipulationen, Schwellenwerte oder die Id des betroffnen VKs
 */
@Data
public class ActionRequestAggregate {
    /**
     * Identifizierung der Maßnahmenabfrage
     */
    private ActionRequestIdVO actionRequestId;
    /**
     * Betroffene VK
     */
    private ActionRequestVirtualPowerPlantIdVO virtualPowerPlantId;
    /**
     * Zeitstempel der Erstellung
     */
    private ActionRequestTimestampVO timestamp;
    /**
     * Aktueller Status der Maßnahmenabfrage
     */
    private ActionRequestStatusVO status;
    /**
     * Schwellenwert in kW für Energieengpässe
     */
    private ActionRequestShortageThresholdVO shortageThreshold;
    /**
     * Schwellenwert in kW für Energieüberschüsse
     */
    private ActionRequestOverflowThresholdVO overflowThreshold;
    /**
     * Menge der Handlungsempfehlungskataloge
     */
    private List<ActionCatalogEntity> catalogs;

    /**
     * Menge der Manipulationen
     */
    private List<ProducerManipulationEntity> producerManipulations;
    private List<StorageManipulationEntity> storageManipulations;
    private List<GridManipulationEntity> gridManipulations;
}