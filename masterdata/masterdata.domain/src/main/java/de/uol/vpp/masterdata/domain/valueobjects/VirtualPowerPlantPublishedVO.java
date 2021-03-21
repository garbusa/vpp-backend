package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Setter
@Getter
public class VirtualPowerPlantPublishedVO {


    private final boolean published;

    public VirtualPowerPlantPublishedVO(boolean published) {
        this.published = published;
    }

}
