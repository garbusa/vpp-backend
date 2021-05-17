package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class GridManipulationStartEndTimestampVO {
    private ZonedDateTime start;
    private ZonedDateTime end;

    public GridManipulationStartEndTimestampVO(Long start, Long end) throws ManipulationException {
        if (start == null || end == null || end < start) {
            throw new ManipulationException("startEndTimestamp", "GridManipulation");
        }

        try {
            this.start = TimestampUtils.toBerlinTimestamp(start, true);
            this.end = TimestampUtils.toBerlinTimestamp(end, true);
        } catch (Exception e) {
            throw new ManipulationException("startEndTimestamp", "GridManipulation", e);
        }
    }

}
