package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import lombok.Getter;
import lombok.Setter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate}
 */
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
