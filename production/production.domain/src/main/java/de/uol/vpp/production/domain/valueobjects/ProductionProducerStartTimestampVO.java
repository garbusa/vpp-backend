package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.utils.TimestampUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ProductionProducerStartTimestampVO {
    private ZonedDateTime timestamp;

    public ProductionProducerStartTimestampVO(Long ts) throws ProductionException {
        if (ts == null) {
            throw new ProductionException("startTimestamp", "ProductionProducer");
        }

        try {
            this.timestamp = TimestampUtils.toBerlinTimestamp(ts);
        } catch (Exception e) {
            throw new ProductionException("startTimestamp", "ProductionProducer", e);
        }
    }

    public boolean isGreater(ProductionProducerStartTimestampVO obj) {
        return timestamp.isAfter(obj.getTimestamp());
    }
}
