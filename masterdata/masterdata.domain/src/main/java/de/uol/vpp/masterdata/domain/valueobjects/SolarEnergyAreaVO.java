package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity}
 */
@Getter
@Setter
public class SolarEnergyAreaVO {
    private Double value;

    public SolarEnergyAreaVO(Double value) throws ProducerException {
        if (value == null || value < 0.) {
            throw new ProducerException("area", "Solaranlage");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
