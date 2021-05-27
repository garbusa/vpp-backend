package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.StorageCapacityVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageLoadTimeHourVO;
import de.uol.vpp.masterdata.domain.valueobjects.StoragePowerVO;
import lombok.Data;

/**
 * Domänen-Entität
 */
@Data
public class StorageEntity {
    /**
     * Identifizierung der Speicheranlage
     */
    private StorageIdVO storageId;
    /**
     * Nennleistung in kW
     */
    private StoragePowerVO storagePower;
    /**
     * Aktuelle Kapazität der Anlage in %.
     * Eine Kapazität von 100% bedeutet, dass der Speicher bereits voll ist.
     */
    private StorageCapacityVO storageCapacity;
    /**
     * Stellt die C-Rate der Speicheranlage dar.
     * Die C-Rate beschreibt die Be- und Entladezeit. Eine C-Rate von 1 beschreibt einer kompletten Ent- oder Beladung
     * von einer Stunde. Eine C-Rate von 0.1 beschreibt eine zehnstündige Ent- oder Beladung.
     */
    private StorageLoadTimeHourVO loadTimeHour;
}
