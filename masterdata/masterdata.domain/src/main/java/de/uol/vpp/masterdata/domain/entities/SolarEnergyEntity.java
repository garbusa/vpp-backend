package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.Data;

/**
 * Domain-Entität einer Solaranlage
 */
@Data
public class SolarEnergyEntity {
    /**
     * Identifizierung der Solaranlage
     */
    private SolarEnergyIdVO id;
    /**
     * Breitengrad vom Standort der Solaranlage
     */
    private SolarEnergyLatitudeVO latitude;
    /**
     * Längengrad vom Standort der Solaranlage
     */
    private SolarEnergyLongitudeVO longitude;
    /**
     * Nennleistung in kWp
     */
    private SolarEnergyRatedCapacityVO ratedCapacity;
    /**
     * Kapazität der Anlage, mit wie viel Prozent-Leistung die Erzeugungsanlage läuft
     * Eine Kapazität von 0% bedeutet, dass die Anlage nicht läuft
     */
    private SolarEnergyCapacityVO capacity;
    /**
     * Die Ausrichtung in Grad Celsius
     * Eine Ausrichtung von 90 Grad Celsius ist eine östliche Ausrichtung der Anlage
     */
    private SolarEnergyAlignmentVO alignment;
    /**
     * Neigungswinkel der Solaranlage im Grad Celsius
     */
    private SolarEnergySlopeVO slope;
}
