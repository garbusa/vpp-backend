package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class HouseholdMemberAmountVO {

    private final Integer amount;

    public HouseholdMemberAmountVO(Integer amount) throws HouseholdException {
        if (amount == null || amount < 0) {
            throw new HouseholdException("validation for household member amount failed");
        }
        this.amount = amount;
    }
}
