package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.Data;

@Data
public class WindEnergyEntity {
    private WindEnergyIdVO id;
    private WindEnergyLatitudeVO latitude;
    private WindEnergyLongitudeVO longitude;
    private WindEnergyEfficiencyVO efficiency;
    private WindEnergyCapacityVO capacity;

    private WindEnergyRadiusVO radius;
    private WindEnergyHeightVO height;
}
