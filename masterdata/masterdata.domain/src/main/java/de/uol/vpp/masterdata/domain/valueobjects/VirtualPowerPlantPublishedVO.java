package de.uol.vpp.masterdata.domain.valueobjects;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VirtualPowerPlantPublishedVO {


    private final boolean value;

    public VirtualPowerPlantPublishedVO(boolean value) {
        this.value = value;
    }

}
