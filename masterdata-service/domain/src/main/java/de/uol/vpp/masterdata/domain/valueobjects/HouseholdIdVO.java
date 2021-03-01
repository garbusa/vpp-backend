package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Setter
@Getter
public class HouseholdIdVO {

    private final String id;

    public HouseholdIdVO(String id) throws HouseholdException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new HouseholdException("validation for household id failed");
        }
        this.id = id;
    }
}
