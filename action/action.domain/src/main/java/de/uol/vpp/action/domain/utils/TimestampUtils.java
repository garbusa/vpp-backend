package de.uol.vpp.action.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimestampUtils {

    public static ZonedDateTime toBerlinTimestamp(Long ts) {
        ZoneId zone = ZoneId.of("Europe/Berlin");
        return Instant.ofEpochSecond(ts).atZone(zone);
    }

}
