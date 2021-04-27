package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;

@Getter
public class LoadHouseholdActionRequestId {
    private String id;

    public LoadHouseholdActionRequestId(String id) throws LoadException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new LoadException("validation for loadhousehold action request id failed");
        }
        this.id = id;
    }

}
