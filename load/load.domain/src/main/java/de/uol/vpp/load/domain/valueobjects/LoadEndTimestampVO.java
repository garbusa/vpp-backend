package de.uol.vpp.load.domain.valueobjects;

import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.utils.TimestampUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadEndTimestampVO {

    private String timestamp;

    public LoadEndTimestampVO(Long ts) throws LoadException {
        if (ts == null) {
            throw new LoadException("load start timestamp validation failed");
        }

        try {
            this.timestamp = TimestampUtils.toBerlinTimestamp(ts);
        } catch (Exception e) {
            throw new LoadException("failed to create timestamp", e);
        }
    }
}
