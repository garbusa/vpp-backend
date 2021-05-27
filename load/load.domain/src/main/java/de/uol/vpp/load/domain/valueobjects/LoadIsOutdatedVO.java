package de.uol.vpp.load.domain.valueobjects;

import lombok.Data;

/**
 * Siehe {@link de.uol.vpp.load.domain.aggregates.LoadAggregate}
 */
@Data
public class LoadIsOutdatedVO {
    private final boolean outdated;
}
