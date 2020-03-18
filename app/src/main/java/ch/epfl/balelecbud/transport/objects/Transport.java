package ch.epfl.balelecbud.transport.objects;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.Date;

public class Transport {

    private TransportType type;
    private int line;
    private String direction;
    private GeoPoint point;
    private Timestamp time;

    public Transport(TransportType type, int line, String direction, GeoPoint point, Timestamp time) {
        this.type = type;
        this.line = line;
        this.direction = direction;
        this.point = point;
        this.time = time;
    }

    public Transport() {}

    public TransportType getType() {
        return type;
    }

    public String getTypeString() { return type.toString(); }

    public int getLine() {
        return line;
    }

    public String getLineString() { return Integer.toString(line); }

    public String getDirection() {
        return direction;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getTimeString() {
        Date date = time.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }
}
