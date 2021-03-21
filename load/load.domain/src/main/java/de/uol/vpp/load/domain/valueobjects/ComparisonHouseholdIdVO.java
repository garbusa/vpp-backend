package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.ComparisonException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparisonHouseholdIdVO {

    private String id;

    public ComparisonHouseholdIdVO(String id) throws ComparisonException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new ComparisonException("validation for householdId failed");
        }
        this.id = id;
    }
}
