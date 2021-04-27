package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.Data;

@Data
public class WaterEnergyEntity {

    private WaterEnergyIdVO id;
    private WaterEnergyEfficiencyVO efficiency;
    private WaterEnergyCapacityVO capacity;

    private WaterEnergyDensityVO density;
    private WaterEnergyGravityVO gravity;
    private WaterEnergyHeightVO height;
    private WaterEnergyVolumeFlowVO volumeFlow;


}
