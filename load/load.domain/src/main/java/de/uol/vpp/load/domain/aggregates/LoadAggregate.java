package de.uol.vpp.load.domain.aggregates;

import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

/**
 * Domänen-Aggregat für die Last.
 * Beinhaltet alle Haushaltslaste für einen bestimmten Zeitpunkt eines VK.
 */
@Data
public class LoadAggregate {
    /**
     * Zu welcher Maßnahmenabfrage die Last zugewiesen ist
     */
    private LoadActionRequestIdVO loadActionRequestId;
    /**
     * Zu welchem VK die Last zugewiesen ist
     */
    private LoadVirtualPowerPlantIdVO loadVirtualPowerPlantId;
    /**
     * Aktueller Zeitpunkt der Last
     */
    private LoadStartTimestampVO loadStartTimestamp;
    /**
     * Aggregierte Last der einzelnen Haushalte innerhalb des VK für einen bestimmten Zeitpunkt
     */
    private List<LoadHouseholdEntity> loadHouseholdEntities;
}
