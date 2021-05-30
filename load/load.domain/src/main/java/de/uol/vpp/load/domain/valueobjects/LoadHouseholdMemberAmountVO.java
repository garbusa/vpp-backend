package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.load.domain.entities.LoadHouseholdEntity}
 */
@Getter
@Setter
public class LoadHouseholdMemberAmountVO {

    private Integer amount;

    public LoadHouseholdMemberAmountVO(Integer amount) throws LoadException {
        if (amount == null || amount < 1) {
            throw new LoadException("householdMemberAmount", "Haushaltslast");
        }
        this.amount = amount;
    }

}
