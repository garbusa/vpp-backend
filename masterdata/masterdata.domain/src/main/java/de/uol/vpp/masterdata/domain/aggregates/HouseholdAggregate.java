package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.aggregates.abstracts.DomainHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdMemberAmountVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domänen-Aggregat für Haushalte
 * Erweitert abstrakte Klasse {@link DomainHasProducersAndStorages}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HouseholdAggregate extends DomainHasProducersAndStorages {
    /**
     * Identifizierung des Haushalts
     */
    private HouseholdIdVO householdId;
    /**
     * Anzahl der Haushaltsmitglieder
     */
    private HouseholdMemberAmountVO householdMemberAmount;
}
