package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyCapacityVO;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyRatedCapacityVO;
import lombok.Data;

@Data
public class OtherEnergyEntity {
    private OtherEnergyIdVO id;
    private OtherEnergyRatedCapacityVO ratedCapacity;
    private OtherEnergyCapacityVO capacity;
}
