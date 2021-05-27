package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.AbstractManipulationEntity}
 */
@Getter
public class ManipulationStartEndTimestampVO {
    private ZonedDateTime start;
    private ZonedDateTime end;

    public ManipulationStartEndTimestampVO(Long start, Long end) throws ManipulationException {
        if (start == null || end == null || end < start) {
            throw new ManipulationException("startEndTimestamp", "Manipulation");
        }

        try {
            this.start = TimestampUtils.toBerlinTimestamp(start, true);
            this.end = TimestampUtils.toBerlinTimestamp(end, true);
        } catch (Exception e) {
            throw new ManipulationException("startEndTimestamp", "Manipulation", e);
        }
    }
}
