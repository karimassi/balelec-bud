package ch.epfl.balelecbud.util;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    public static String timestampToScheduleString(Timestamp time) {
        Date date = time.toDate();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return dtf.format(date);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm", Locale.ENGLISH);
        return dateFormat.format(date);
    }
}
