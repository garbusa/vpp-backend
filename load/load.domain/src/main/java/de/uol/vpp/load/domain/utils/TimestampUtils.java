package de.uol.vpp.load.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Util-Klasse für die Erstellung eines Zeitstempels mit einer Zone
 */
public class TimestampUtils {

    /**
     * Erstellt ein Zeitstempel mit einer Berliner Zeitzone und berücksichtigt,
     * ob der Zeitstempel auf eine viertelstunde abgerundet werden soll.
     *
     * @param ts        Zeitstempel
     * @param isQuarter soll abgerundet werden?
     * @return ZonedDateTime
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
