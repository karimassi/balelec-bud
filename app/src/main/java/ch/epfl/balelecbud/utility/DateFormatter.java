package ch.epfl.balelecbud.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Simple static util class to centralize all date formatting
 */
public final class DateFormatter {

    public enum Verbosity {
        IN_YEAR("yyyy.MM.dd 'at' HH:mm:ss", Locale.ENGLISH),
        IN_DAY("HH:mm", Locale.ENGLISH);

        Verbosity(String format, Locale local){
            sdf = new SimpleDateFormat(format, local);
        }

        private final SimpleDateFormat sdf;

        public String format(Long date){
            return format(new Date(date));
        }

        public String format(Date date){
            return sdf.format(date);
        }
    }
}
