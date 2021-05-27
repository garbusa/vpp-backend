package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

/**
 * Domänen-Entität der Handlungsempfehlungen
 */
@Data
public class ActionEntity {
    /**
     * Objekte zur Identifizierung einer Handlungsempfehlung, bestehend aus:
     * - Identifizierung eines Handlungsempfehlungekatalog
     * - Identifizierung einer Speicher oder Erzeugungsanlage
     * - Typ der Handlungsempfehlung
     */
    private ActionRequestIdVO actionRequestId;
    private ActionCatalogStartTimestampVO startTimestamp;
    private ActionCatalogEndTimestampVO endTimestamp;
    private ActionTypeVO actionType;
    private ActionProducerOrStorageIdVO producerOrStorageId;
    /**
     * Gibt an ob Handlungsempfehlung für eine Speicheranlage gedacht ist
     */
    private ActionIsStorageVO isStorage;
    /**
     * potenziell regelbare Energie
     */
    private ActionValueVO actionValue;
    /**
     * wenn {@link ActionEntity#isStorage} wahr, dann wird mögliche Ladezeit in Stunden gespeichert
     */
    private ActionHoursVO hours;
}
