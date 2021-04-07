package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadHouseholdValueVO {
    private Double value;

    public LoadHouseholdValueVO(Double value) throws LoadException {
        if (value == null || value < 0) {
            throw new LoadException("validation for load loadValueSum failed.");
        }
        this.value = value;
    }
}
