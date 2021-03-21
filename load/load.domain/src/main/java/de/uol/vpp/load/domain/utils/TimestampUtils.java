package de.uol.vpp.load.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimestampUtils {

    public static String toBerlinTimestamp(Long ts) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
        ZoneId zone = ZoneId.of("Europe/Berlin");
        ZonedDateTime dateTime = Instant.ofEpochSecond(ts).atZone(zone);
        return dateTime.format(formatter);
    }

}
