package ch.epfl.balelecbud.utility.notifications.concertFlow.objects;

import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;


public class Converters {
    @TypeConverter
    public static Timestamp fromString(String value) {
        if (value == null)
            return null;

        String[] slip = value.split(",");
        return new Timestamp(Long.parseLong(slip[0]), Integer.parseInt(slip[1]));
    }
    @TypeConverter
    public static String fromTimestamp(Timestamp value) {
        if (value == null)
            return null;

        return value.getSeconds() + "," + value.getNanoseconds();
    }
}
