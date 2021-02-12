package de.uol.vpp.masterdata.domain.household;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class HouseholdId {
    private String id;
}
