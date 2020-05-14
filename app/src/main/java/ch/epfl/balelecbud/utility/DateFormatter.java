package ch.epfl.balelecbud.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Simple static util class to centralize all date formatting
 */
public class DateFormatter {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss", Locale.ENGLISH);

    public static String format(Long date){
        return format(new Date(date));
    }

    public static String format(Date date){
        return sdf.format(date);
    }
}
