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
public class SolarEnergyLatitudeVO {

    private Double value;

    public SolarEnergyLatitudeVO(Double value) throws ProducerException {
        if (value == null || value < -90. || value > 90.) {
            throw new ProducerException("latitude", "Solaranlage");
        }
        this.value = value;
    }
}
