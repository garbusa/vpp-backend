package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

import java.util.List;

/**
 * Domänen-Entität der Handlungsempfehlungskataloge
 */
@Data
public class ActionCatalogEntity {
    /**
     * Identifizierung der Maßnahmenabfrage, ist Teil der Identifizierung
     * eines Handlungsempfehlungskatalog
     */
    private ActionRequestIdVO actionRequestIdVO;
    /**
     * Startzeitstempel
     */
    private ActionCatalogStartTimestampVO startTimestamp;
    /**
     * Endzeitstempel
     */
    private ActionCatalogEndTimestampVO endTimestamp;
    /**
     * Art des Problems (SHORTAGE, OVERFLOW)
     */
    private ActionCatalogProblemTypeVO problemType;
    /**
     * durchschnittliche Energielücker innerhalb des Zeitraums
     */
    private ActionCatalogAverageGapVO averageGap;
    /**
     * Menge aller Handlungsempfehlungen nach Fertigstellung der Maßnahmenabfrage
     */
    private List<ActionEntity> actions;
}
