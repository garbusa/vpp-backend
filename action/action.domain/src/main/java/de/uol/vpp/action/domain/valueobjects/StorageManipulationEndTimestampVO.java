package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.StorageManipulationEntity}
 */
@Getter
public class StorageManipulationEndTimestampVO {
    private ZonedDateTime value;

    public StorageManipulationEndTimestampVO(Long ts) throws ManipulationException {
        if (ts == null) {
            throw new ManipulationException("startTimestamp", "StorageManipulation");
        }

        try {
            this.value = TimestampUtils.toBerlinTimestamp(ts, true);
        } catch (Exception e) {
            throw new ManipulationException("startTimestamp", "StorageManipulation", e);
        }
    }

    public boolean isGreater(ActionRequestTimestampVO obj) {
        return value.isAfter(obj.getValue());
    }


}
