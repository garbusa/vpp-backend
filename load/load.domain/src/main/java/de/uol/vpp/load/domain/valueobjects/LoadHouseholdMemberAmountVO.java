package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadHouseholdMemberAmountVO {

    private Integer amount;

    public LoadHouseholdMemberAmountVO(Integer amount) throws LoadException {
        if (amount == null || amount < 1) {
            throw new LoadException("validation for load household member amount failed.");
        }
        this.amount = amount;
    }

}
