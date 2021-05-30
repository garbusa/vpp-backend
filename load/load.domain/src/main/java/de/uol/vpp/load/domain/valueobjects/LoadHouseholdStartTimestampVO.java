package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.utils.TimestampUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.load.domain.entities.LoadHouseholdEntity}
 */
@Getter
@Setter
public class LoadHouseholdStartTimestampVO {
    private ZonedDateTime timestamp;

    public LoadHouseholdStartTimestampVO(Long ts) throws LoadException {
        if (ts == null) {
            throw new LoadException("startTimestamp", "Haushaltslast");
        }

        try {
            this.timestamp = TimestampUtils.toBerlinTimestamp(ts, false);
        } catch (Exception e) {
            throw new LoadException("startTimestamp", "Haushaltslast", e);
        }
    }

    public boolean isGreater(LoadHouseholdStartTimestampVO obj) {
        return timestamp.isAfter(obj.getTimestamp());
    }
}
