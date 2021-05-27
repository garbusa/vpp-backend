package de.uol.vpp.action.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Helferfunktionen für die Behandlung von Zeitstempel
 */
public class TimestampUtils {

    /**
     * Konvertierung eines unix-Zeitstempels in ein Datumsobjekt mit einer ZoneId (Berlin).
     * Berücksichtigt zusätzlich, ob eine viertelstündige Abrundung der Zeit notwendig ist
     *
     * @param ts        unix-Zeitstempel
     * @param isQuarter Soll es eine viertelstündige Abrundung der Zeit geben?
     * @return Datumsobjekt mit einer ZoneId
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
