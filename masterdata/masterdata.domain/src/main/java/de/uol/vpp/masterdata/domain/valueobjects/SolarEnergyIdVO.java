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
public class SolarEnergyIdVO {


    private final String value;

    public SolarEnergyIdVO(String value) throws ProducerException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ProducerException("solarEnergyId", "Solaranlage");
        }
        this.value = value.toUpperCase();
    }

}
