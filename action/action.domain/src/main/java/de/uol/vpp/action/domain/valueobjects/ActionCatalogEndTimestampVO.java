package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class ActionCatalogEndTimestampVO {
    private ZonedDateTime value;

    public ActionCatalogEndTimestampVO(Long ts) throws ActionException {
        if (ts == null) {
            throw new ActionException("endTimestamp", "Action");
        }

        try {
            this.value = TimestampUtils.toBerlinTimestamp(ts);
        } catch (Exception e) {
            throw new ActionException("endTimestamp", "Action", e);
        }
    }

    public boolean isGreater(ActionRequestTimestampVO obj) {
        return value.isAfter(obj.getValue());
    }


}
