package ch.epfl.balelecbud.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    public static String timestampToScheduleString(Timestamp time) {
        Date date = time.toDate();
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return dtf.format(date);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(date);
    }
}
