package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import lombok.Getter;
import lombok.Setter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate}
 */
@Getter
@Setter
public class VirtualPowerPlantIdVO {

    private final String value;

    public VirtualPowerPlantIdVO(String value) throws VirtualPowerPlantException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new VirtualPowerPlantException("virtualPowerPlantId");
        }
        this.value = value.toUpperCase();
    }
}
