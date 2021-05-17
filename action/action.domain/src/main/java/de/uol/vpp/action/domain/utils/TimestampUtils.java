package de.uol.vpp.action.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimestampUtils {

    public static ZonedDateTime toBerlinTimestamp(Long ts, boolean isQuarter) {
        ZoneId zone = ZoneId.of("Europe/Berlin");
        ZonedDateTime zdt = Instant.ofEpochSecond(ts).atZone(zone);
        if (isQuarter) {
            zdt = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),
                    zdt.getDayOfMonth(), zdt.getHour(), zdt.getMinute() - (zdt.getMinute() % 15), 0, 0,
                    zdt.getZone());
        }
        return zdt;
    }

}
