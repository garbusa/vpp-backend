package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.load.domain.entities.LoadHouseholdEntity}
 */
@Getter
@Setter
public class LoadHouseholdIdVO {


    private String id;

    public LoadHouseholdIdVO(String id) throws LoadException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new LoadException("householdId", "Haushaltslast");
        }
        this.id = id;
    }

}
