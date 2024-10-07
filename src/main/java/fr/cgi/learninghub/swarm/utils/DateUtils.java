package fr.cgi.learninghub.swarm.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtils() {
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate.format(DATE_FORMATTER);
    }
}
