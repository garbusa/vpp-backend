package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import lombok.Getter;
import lombok.Setter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate}
 */
@Setter
@Getter
public class DecentralizedPowerPlantIdVO {

    private final String value;

    public DecentralizedPowerPlantIdVO(String value) throws DecentralizedPowerPlantException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new DecentralizedPowerPlantException("decentralizedPowerPlantId");
        }
        this.value = value.toUpperCase();
    }
}
