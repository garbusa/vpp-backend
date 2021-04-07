package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.utils.TimestampUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class LoadHouseholdStartTimestampVO {
    private ZonedDateTime timestamp;

    public LoadHouseholdStartTimestampVO(Long ts) throws LoadException {
        if (ts == null) {
            throw new LoadException("load household startTimestamp validation failed");
        }

        try {
            this.timestamp = TimestampUtils.toBerlinTimestamp(ts);
        } catch (Exception e) {
            throw new LoadException("failed to create startTimestamp", e);
        }
    }
}
