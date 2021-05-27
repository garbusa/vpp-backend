package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyCapacityVO;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyRatedCapacityVO;
import lombok.Data;

/**
 * Domain-Entität einer alternativen Erzeugungsanlage mit konstanter Leistung
 */
@Data
public class OtherEnergyEntity {
    /**
     * Identifizierung der Erzeugungsanlage
     */
    private OtherEnergyIdVO id;
    /**
     * Nennleistung in kW
     */
    private OtherEnergyRatedCapacityVO ratedCapacity;
    /**
     * Kapazität der Anlage, mit wie viel %-Leistung die Erzeugungsanlage läuft
     * Eine Kapazität von 0% bedeutet, dass die Anlage nicht läuft
     */
    private OtherEnergyCapacityVO capacity;
}
