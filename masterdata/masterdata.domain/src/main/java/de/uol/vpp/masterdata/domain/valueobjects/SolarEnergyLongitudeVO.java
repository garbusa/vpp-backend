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
public class SolarEnergyLongitudeVO {

    private Double value;

    public SolarEnergyLongitudeVO(Double value) throws ProducerException {
        if (value == null || value < -180. || value > 180.) {
            throw new ProducerException("longitude", "Solaranlage");
        }
        this.value = value;
    }
}
