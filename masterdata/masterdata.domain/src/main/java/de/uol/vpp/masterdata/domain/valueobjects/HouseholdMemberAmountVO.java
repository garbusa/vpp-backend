package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseholdMemberAmountVO {

    private final Integer value;

    public HouseholdMemberAmountVO(Integer value) throws HouseholdException {
        if (value == null || value < 0) {
            throw new HouseholdException("householdMemberAmount");
        }
        this.value = value;
    }
}
