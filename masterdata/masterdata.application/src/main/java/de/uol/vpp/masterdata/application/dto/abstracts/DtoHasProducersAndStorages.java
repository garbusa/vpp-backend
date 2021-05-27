package de.uol.vpp.masterdata.application.dto.abstracts;

import de.uol.vpp.masterdata.application.dto.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraktes DTO, children besitzen Erzeugungsanlagen und Speicheranlagen
 */
@Data
public abstract class DtoHasProducersAndStorages {
    private List<SolarEnergyDTO> solars = new ArrayList<>();
    private List<WindEnergyDTO> winds = new ArrayList<>();
    private List<WaterEnergyDTO> waters = new ArrayList<>();
    private List<OtherEnergyDTO> others = new ArrayList<>();
    private List<StorageDTO> storages = new ArrayList<>();
}
