package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.ComparisonException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparisonVirtualPowerPlantIdVO {

    private String id;

    public ComparisonVirtualPowerPlantIdVO(String id) throws ComparisonException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new ComparisonException("validation for vppId failed.");
        }
        this.id = id;
    }
}
