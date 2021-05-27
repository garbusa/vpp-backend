package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.StorageManipulationHoursVO;
import de.uol.vpp.action.domain.valueobjects.StorageManipulationRatedPowerVO;
import de.uol.vpp.action.domain.valueobjects.StorageManipulationStorageIdVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domänen-Entität für Speichermanipulationen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StorageManipulationEntity extends AbstractManipulationEntity {
    /**
     * Gibt an welche Speicheranlage in der Manipulation betroffen ist
     */
    private StorageManipulationStorageIdVO storageId;
    /**
     * Gibt an, wie viele Stunden der Speicher be- oder entladen wird
     */
    private StorageManipulationHoursVO hours;
    /**
     * Gibt die Leistung in kW an, wie viel Energie be- oder entladen wird
     */
    private StorageManipulationRatedPowerVO ratedPower;

}
