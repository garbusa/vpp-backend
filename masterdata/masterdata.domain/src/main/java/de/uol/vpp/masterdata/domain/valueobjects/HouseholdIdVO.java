package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HouseholdIdVO {

    private final String value;

    public HouseholdIdVO(String value) throws HouseholdException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new HouseholdException("householdId");
        }
        this.value = value.toUpperCase();
    }
}
