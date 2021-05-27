package de.uol.vpp.production.domain.entities;

import de.uol.vpp.production.domain.valueobjects.*;
import lombok.Data;

/**
 * Domänen-Aggregat für den Erzeugungswert einer Erzeugungsanlage
 * Dieses Aggregat beinhaltet die Erzeugungsanlage mit dem jeweiligen Typ, sowie Informationen
 * über die im aktuellen Zeitpunkt höchstmögliche Erzeugung, sowie die aktuelle/tatsächliche Erzeugung
 */
@Data
public class ProductionProducerEntity {
    /**
     * Erzeugung ist einer Erzeugungsanlage zugewiesen
     */
    private ProductionProducerIdVO producerId;
    /**
     * Typ der Erzeugungsanlage (z.B. WIND)
     */
    private ProductionProducerTypeVO productionType;
    /**
     * Aktueller Zeitpunkt
     */
    private ProductionProducerStartTimestampVO startTimestamp;
    /**
     * Tatsächlicher Erzeugungswert zum Zeitpunkt {@link ProductionProducerEntity#startTimestamp}
     */
    private ProductionProducerCurrentValueVO currentValue;
    /**
     * Höchstmöglicher Erzeugungswert zum Zeitpunkt {@link ProductionProducerEntity#startTimestamp}
     * bei einer Kapazität von 100%
     */
    private ProductionProducerPossibleValueVO possibleValue;
}
