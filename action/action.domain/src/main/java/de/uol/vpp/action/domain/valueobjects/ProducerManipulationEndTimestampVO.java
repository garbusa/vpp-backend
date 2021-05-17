package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class ProducerManipulationEndTimestampVO {
    private ZonedDateTime value;

    public ProducerManipulationEndTimestampVO(Long ts) throws ManipulationException {
        if (ts == null) {
            throw new ManipulationException("startTimestamp", "ProducerManipulation");
        }

        try {
            this.value = TimestampUtils.toBerlinTimestamp(ts, true);
        } catch (Exception e) {
            throw new ManipulationException("startTimestamp", "ProducerManipulation", e);
        }
    }

    public boolean isGreater(ActionRequestTimestampVO obj) {
        return value.isAfter(obj.getValue());
    }


}
