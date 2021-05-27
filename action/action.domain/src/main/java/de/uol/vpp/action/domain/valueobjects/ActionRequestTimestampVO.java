package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.action.domain.aggregates.ActionRequestAggregate}
 */
@Getter
public class ActionRequestTimestampVO {
    private ZonedDateTime value;

    public ActionRequestTimestampVO(Long ts) throws ActionException {
        if (ts == null) {
            throw new ActionException("timestamp", "ActionRequest");
        }

        try {
            this.value = TimestampUtils.toBerlinTimestamp(ts, false);
        } catch (Exception e) {
            throw new ActionException("timestamp", "ActionRequest", e);
        }
    }

    public boolean isGreater(ActionRequestTimestampVO obj) {
        return value.isAfter(obj.getValue());
    }

}
