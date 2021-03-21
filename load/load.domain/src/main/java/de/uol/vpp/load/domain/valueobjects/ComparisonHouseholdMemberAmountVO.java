package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.ComparisonException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparisonHouseholdMemberAmountVO {

    private Integer amount;

    public ComparisonHouseholdMemberAmountVO(Integer amount) throws ComparisonException {
        if (amount == null || amount < 0) {
            throw new ComparisonException("validation for household member amount failed.");
        }
        this.amount = amount;
    }
}
