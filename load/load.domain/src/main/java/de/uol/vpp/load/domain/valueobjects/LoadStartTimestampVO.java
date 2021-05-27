package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.utils.TimestampUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.load.domain.aggregates.LoadAggregate}
 */
@Getter
@Setter
public class LoadStartTimestampVO {

    private ZonedDateTime timestamp;

    public LoadStartTimestampVO(Long ts) throws LoadException {
        if (ts == null) {
            throw new LoadException("startTimestamp", "Load");
        }

        try {
            this.timestamp = TimestampUtils.toBerlinTimestamp(ts, false);
        } catch (Exception e) {
            throw new LoadException("startTimestamp", "Load");
        }
    }

    public boolean isGreater(LoadStartTimestampVO obj) {
        return timestamp.isAfter(obj.getTimestamp());
    }

}
