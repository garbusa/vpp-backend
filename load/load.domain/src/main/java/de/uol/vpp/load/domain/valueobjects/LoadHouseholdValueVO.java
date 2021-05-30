package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.load.domain.entities.LoadHouseholdEntity}
 */
@Getter
@Setter
public class LoadHouseholdValueVO {
    private Double value;

    public LoadHouseholdValueVO(Double value) throws LoadException {
        if (value == null || value < 0) {
            throw new LoadException("value", "Haushaltslast");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
