package ch.epfl.balelecbud.utility;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Simple enum to centralize all date formatting
 */
public enum DateFormatter {
    IN_YEAR("yyyy.MM.dd 'at' HH:mm:ss", Locale.ENGLISH),
    IN_DAY("HH:mm", Locale.ENGLISH),
    FILE_TIMESTAMP("yyyyMMdd_HHmmss", Locale.ENGLISH),
    TRANSPORT_TIME("dd-MM HH:mm", Locale.ENGLISH);

    DateFormatter(String format, Locale local) {
        sdf = new SimpleDateFormat(format, local);
    }

    private final SimpleDateFormat sdf;

    public String format(Timestamp date) {
        return format(date.toDate());
    }

    public String format(Long date) {
        return format(new Date(date));
    }

    public String format(Date date) {
        return sdf.format(date);
    }
}
