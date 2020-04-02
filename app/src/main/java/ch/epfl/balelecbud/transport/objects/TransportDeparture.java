package ch.epfl.balelecbud.transport.objects;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.Objects;

import ch.epfl.balelecbud.util.StringUtils;

public class TransportDeparture {

    private String category;
    private String line;
    private String destination;
    private Date time;

    public TransportDeparture(String category, String line, String destination, Date time) {
        this.category = category;
        this.line = line;
        this.destination = destination;
        this.time = time;
    }

    public TransportDeparture() {

    }

    public String getCategory() {
        return category;
    }

    public String getLine() {
        return line;
    }

    public String getDestination() {
        return destination;
    }

    public Date getTime() {
        return time;
    }

    public String getTimeString() {
        return StringUtils.dateToString(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportDeparture that = (TransportDeparture) o;
        return Objects.equals(getCategory(), that.getCategory()) &&
                Objects.equals(getLine(), that.getLine()) &&
                Objects.equals(getDestination(), that.getDestination()) &&
                Objects.equals(getTime(), that.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getLine(), getDestination(), getTime());
    }
}
