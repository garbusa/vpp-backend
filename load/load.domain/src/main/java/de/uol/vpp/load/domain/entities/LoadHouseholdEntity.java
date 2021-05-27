package de.uol.vpp.load.domain.entities;

import de.uol.vpp.load.domain.valueobjects.LoadHouseholdIdVO;
import de.uol.vpp.load.domain.valueobjects.LoadHouseholdMemberAmountVO;
import de.uol.vpp.load.domain.valueobjects.LoadHouseholdStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadHouseholdValueVO;
import lombok.Data;

/**
 * Domänen-Entität, die die Last eines Haushaltes für einen bestimmten Zeitpunkt besitzt.
 */
@Data
public class LoadHouseholdEntity {
    /**
     * Zu welchem Haushalt die Last gehört
     */
    private LoadHouseholdIdVO loadHouseholdId;
    /**
     * Zeitstempel der Last
     */
    private LoadHouseholdStartTimestampVO loadHouseholdStartTimestamp;
    /**
     * Anzahl der Haushaltsmitglieder
     */
    private LoadHouseholdMemberAmountVO loadHouseholdMemberAmount;
    /**
     * Die Last
     */
    private LoadHouseholdValueVO loadHouseholdValueVO;
}
