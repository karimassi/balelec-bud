package ch.epfl.balelecbud.Transport.Object;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

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

    public int getLine() {
        return line;
    }

    public String getDirection() {
        return direction;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public Timestamp getTime() {
        return time;
    }
}
