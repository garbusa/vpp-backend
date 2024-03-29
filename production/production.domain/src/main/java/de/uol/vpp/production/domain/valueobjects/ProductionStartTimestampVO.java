package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.utils.TimestampUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * Siehe {@link de.uol.vpp.production.domain.aggregates.ProductionAggregate}
 */
@Getter
@Setter
public class ProductionStartTimestampVO {
    private ZonedDateTime timestamp;

    public ProductionStartTimestampVO(Long ts) throws ProductionException {
        if (ts == null) {
            throw new ProductionException("startTimestamp", "Erzeugungsaggregat");
        }

        try {
            this.timestamp = TimestampUtils.toBerlinTimestamp(ts, false);
        } catch (Exception e) {
            throw new ProductionException("startTimestamp", "Erzeugungsaggregat", e);
        }
    }

    public boolean isGreater(ProductionStartTimestampVO obj) {
        return timestamp.isAfter(obj.getTimestamp());
    }
}
