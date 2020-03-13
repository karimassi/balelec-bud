package ch.epfl.balelecbud.Transport.Object;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Transport {

    public static List<Transport> DUMMY_TRANSPORTS = new LinkedList<>();
    static {
        DUMMY_TRANSPORTS.add(new Transport(TransportType.BUS, 10, "Nyon", null, Timestamp.now()));
        DUMMY_TRANSPORTS.add(new Transport(TransportType.METRO, 11, "Lausanne", null, Timestamp.now()));
        DUMMY_TRANSPORTS.add(new Transport(TransportType.BUS, 12, "La lune", null, Timestamp.now()));
        DUMMY_TRANSPORTS.add(new Transport(TransportType.METRO, 13, "Chine", null, Timestamp.now()));
    }

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
