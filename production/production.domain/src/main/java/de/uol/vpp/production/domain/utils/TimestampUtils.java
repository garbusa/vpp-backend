package de.uol.vpp.production.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Util-Klasse die für die Umwandlung eines Timestamp in ein ZonedDateTime Objekt zuständig ist
 */
public class TimestampUtils {

    /**
     * Wandelt ein unix-Timestamp in ein ZonedDateTime um und berücksichtigt zusätzlich,
     * ob aktueller Zeitstempel auf eine viertelstunde abgerundet werden soll
     *
     * @param ts        unix-Timestamp
     * @param isQuarter soll auf eine viertelstunde abgerundet werden?
     * @return ZonedDateTime in Berliner Zeitzone
     */
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
