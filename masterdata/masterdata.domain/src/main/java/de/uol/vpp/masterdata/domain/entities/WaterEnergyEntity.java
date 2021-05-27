package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.Data;

/**
 * Domänen-Entität der Wasserkraftanlagen
 */
@Data
public class WaterEnergyEntity {
    /**
     * Identifizierung der Wasserkraftanlage
     */
    private WaterEnergyIdVO id;
    /**
     * Wirkungsgrad der Wasserkraftanlage in Prozent
     */
    private WaterEnergyEfficiencyVO efficiency;
    /**
     * Kapazität der Anlage, mit wie viel Prozent-Leistung die Erzeugungsanlage läuft
     * Eine Kapazität von 0% bedeutet, dass die Anlage nicht läuft
     */
    private WaterEnergyCapacityVO capacity;
    /**
     * Wasserdichte in kg/m^3
     */
    private WaterEnergyDensityVO density;
    /**
     * Fallbeschleunigung des Wassers in m/s^2
     */
    private WaterEnergyGravityVO gravity;
    /**
     * Nutzbare Fallhöhe in Meter
     */
    private WaterEnergyHeightVO height;
    /**
     * Volumenstrom des Wassers in m^3/s
     */
    private WaterEnergyVolumeFlowVO volumeFlow;


}
