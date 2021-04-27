package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class ActionRequestTimestampVO {
    private ZonedDateTime value;

    public ActionRequestTimestampVO(Long ts) throws ActionException {
        if (ts == null) {
            throw new ActionException("validation for action request timestamp failed");
        }

        try {
            this.value = TimestampUtils.toBerlinTimestamp(ts);
        } catch (Exception e) {
            throw new ActionException("failed to create startTimestamp", e);
        }
    }

    public boolean isGreater(ActionRequestTimestampVO obj) {
        return value.isAfter(obj.getValue());
    }

}
