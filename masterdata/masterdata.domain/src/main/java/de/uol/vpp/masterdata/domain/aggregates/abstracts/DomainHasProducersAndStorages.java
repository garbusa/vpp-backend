package de.uol.vpp.masterdata.domain.aggregates.abstracts;

import de.uol.vpp.masterdata.domain.entities.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakte Klasse für Haushalte und DK
 * Enthält die Speicher- und Erzeugungsanlagen
 */
@Data
public abstract class DomainHasProducersAndStorages {
    /**
     * Solaranlagen
     */
    private List<SolarEnergyEntity> solars = new ArrayList<>();
    /**
     * Windkraftanlagen
     */
    private List<WindEnergyEntity> winds = new ArrayList<>();
    /**
     * Wasserkraftanlagen
     */
    private List<WaterEnergyEntity> waters = new ArrayList<>();
    /**
     * alternative Erzeugungsanlagen mit konstanter Leistung
     */
    private List<OtherEnergyEntity> others = new ArrayList<>();
    /**
     * Speicheranlagen
     */
    private List<StorageEntity> storages = new ArrayList<>();
}
