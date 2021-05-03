package de.uol.vpp.masterdata.domain.aggregates.abstracts;

import de.uol.vpp.masterdata.domain.entities.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class DomainHasProducersAndStorages {
    private List<SolarEnergyEntity> solars = new ArrayList<>();
    private List<WindEnergyEntity> winds = new ArrayList<>();
    private List<WaterEnergyEntity> waters = new ArrayList<>();
    private List<OtherEnergyEntity> others = new ArrayList<>();
    private List<StorageEntity> storages = new ArrayList<>();
}
