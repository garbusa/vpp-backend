package de.uol.vpp.load.domain.entities;

import de.uol.vpp.load.domain.valueobjects.ComparisonHouseholdIdVO;
import de.uol.vpp.load.domain.valueobjects.ComparisonHouseholdMemberAmountVO;
import lombok.Data;

@Data
public class ComparisonHouseholdEntity {
    private final ComparisonHouseholdIdVO comparisonHouseholdIdVO;
    private final ComparisonHouseholdMemberAmountVO comparisonHouseholdMemberAmountVO;
}
