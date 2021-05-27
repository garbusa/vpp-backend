package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.Data;

/**
 * Domänen-Entität einer Windkraftanlage
 */
@Data
public class WindEnergyEntity {
    /**
     * Identifizierung der Windkraftanlage
     */
    private WindEnergyIdVO id;
    /**
     * Breitengrad des Standorts der Windkraftanlage
     */
    private WindEnergyLatitudeVO latitude;
    /**
     * Längengrad des Standorts der Windkraftanlage
     */
    private WindEnergyLongitudeVO longitude;
    /**
     * Wirkungsgrad der Windkraftanlage in Prozent
     */
    private WindEnergyEfficiencyVO efficiency;
    /**
     * Kapazität der Anlage, mit wie viel Prozent-Leistung die Erzeugungsanlage läuft
     * Eine Kapazität von 0% bedeutet, dass die Anlage nicht läuft
     */
    private WindEnergyCapacityVO capacity;
    /**
     * Radius der Turbine in Meter
     */
    private WindEnergyRadiusVO radius;
    /**
     * Höhe der Windkraftanlage
     */
    private WindEnergyHeightVO height;
}
