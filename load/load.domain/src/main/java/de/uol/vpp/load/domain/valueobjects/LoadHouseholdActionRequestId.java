package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.load.domain.entities.LoadHouseholdEntity}
 */
@Getter
public class LoadHouseholdActionRequestId {
    private String id;

    public LoadHouseholdActionRequestId(String id) throws LoadException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new LoadException("actionRequestId", "Haushaltslast");
        }
        this.id = id;
    }

}
