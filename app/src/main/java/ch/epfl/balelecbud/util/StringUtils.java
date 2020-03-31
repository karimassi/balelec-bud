package ch.epfl.balelecbud.util;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    public static String timestampToScheduleString(Timestamp time) {
        Date date = time.toDate();
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm");
        return dtf.format(date);
    }
}
