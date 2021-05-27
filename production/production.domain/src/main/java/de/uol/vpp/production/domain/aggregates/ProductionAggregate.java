package de.uol.vpp.production.domain.aggregates;

import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;
import de.uol.vpp.production.domain.valueobjects.ProductionStartTimestampVO;
import de.uol.vpp.production.domain.valueobjects.ProductionVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

/**
 * Domänen-Aggregat für die Erzeugungsaggregation
 * Dieses Aggregat stellt eine Zusammenfassung der Erzeugungswerte aller Produktionsanlagen zu einem bestimmten Zeitpunkt
 * Ein Menge von Aggregaten (97 Stück, 24 Stunden * 4 = 97 Viertelstunden) ergibt eine Erzeugungsprognose von einem Tag.
 */
@Data
public class ProductionAggregate {
    /**
     * Erzeugungsaggregat gehört einer Maßnahmenabfrage an
     */
    private ProductionActionRequestIdVO productionActionRequestId;
    /**
     * Erzeugungsaggregat gehört einem VK an (indirekt durch Maßnahmenabfrage)
     */
    private ProductionVirtualPowerPlantIdVO productionVirtualPowerPlantId;
    /**
     * Aktueller Zeitstempel
     */
    private ProductionStartTimestampVO productionStartTimestamp;
    /**
     * Liste der Erzeugungswerte für jede Erzeugungsanlage innerhalb des VK im aktuellen Zeitraum
     */
    private List<ProductionProducerEntity> productionProducers;
}
