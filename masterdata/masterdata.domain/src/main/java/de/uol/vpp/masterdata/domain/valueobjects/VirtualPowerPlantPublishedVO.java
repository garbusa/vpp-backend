package de.uol.vpp.masterdata.domain.valueobjects;

import lombok.Getter;
import lombok.Setter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate}
 */
@Setter
@Getter
public class VirtualPowerPlantPublishedVO {


    private final boolean value;

    public VirtualPowerPlantPublishedVO(boolean value) {
        this.value = value;
    }

}
