package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.load.domain.aggregates.LoadAggregate}
 */
@Getter
@Setter
public class LoadActionRequestIdVO {

    private final String id;

    public LoadActionRequestIdVO(String id) throws LoadException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new LoadException("actionRequestId", "Load");
        }
        this.id = id.toUpperCase();
    }
}
