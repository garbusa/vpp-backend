package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantPublishedVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Domänen-Aggregat für virtuelle Kraftwerke
 * Ist das höchstmögliche Objekt und beinhaltet alle DK und Haushalte mit deren Anlagen
 */
@Data
public class VirtualPowerPlantAggregate {
    /**
     * Identifizierung des VK
     */
    private VirtualPowerPlantIdVO virtualPowerPlantId;
    /**
     * Menge aller Haushalte im VK
     */
    private List<HouseholdAggregate> households = new ArrayList<>();
    /**
     * Menge aller dezentraler Kraftwerke im VK
     */
    private List<DecentralizedPowerPlantAggregate> decentralizedPowerPlants = new ArrayList<>();
    /**
     * Veröffentlicht/nicht veröffentlicht
     */
    private VirtualPowerPlantPublishedVO published;
}
