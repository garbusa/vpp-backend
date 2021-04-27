package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.Data;

@Data
public class SolarEnergyEntity {
    private SolarEnergyIdVO id;
    private SolarEnergyLatitudeVO latitude;
    private SolarEnergyLongitudeVO longitude;
    private SolarEnergyRatedCapacityVO ratedCapacity;
    private SolarEnergyCapacityVO capacity;

    private SolarEnergyAlignmentVO alignment;
    private SolarEnergySlopeVO slope;
}
