package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.utils.TimestampUtils;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionCatalogEntity}
 */
@Getter
public class ActionCatalogStartTimestampVO {
    private ZonedDateTime value;

    public ActionCatalogStartTimestampVO(Long ts) throws ActionException {
        if (ts == null) {
            throw new ActionException("startTimestamp", "Handlungsempfehlungskatalog");
        }

        try {
            this.value = TimestampUtils.toBerlinTimestamp(ts, false);
        } catch (Exception e) {
            throw new ActionException("startTimestamp", "Handlungsempfehlungskatalog", e);
        }
    }

    public boolean isGreater(ActionRequestTimestampVO obj) {
        return value.isAfter(obj.getValue());
    }


}
