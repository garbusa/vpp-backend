package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity}
 */
@Getter
public class OtherEnergyRatedCapacityVO {
    private Double value;

    public OtherEnergyRatedCapacityVO(Double value) throws ProducerException {
        if (value == null || value < 0.) {
            throw new ProducerException("ratedCapacity", "OtherEnergy");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
