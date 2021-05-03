package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.aggregates.abstracts.DomainHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdMemberAmountVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HouseholdAggregate extends DomainHasProducersAndStorages {
    private HouseholdIdVO householdId;
    private HouseholdMemberAmountVO householdMemberAmount;
}
