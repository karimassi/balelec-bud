package ch.epfl.balelecbud.model;

import java.util.Date;
import java.util.Objects;

import ch.epfl.balelecbud.utility.DateFormatters;

/**
 * Class modeling a public transport departure
 */
public final class TransportDeparture {

    private String category;
    private String line;
    private String destination;
    private Date time;

    /**
     * Constructor for a transport departure
     *
     * @param category    the category of the transport
     * @param line        the line of the transport
     * @param destination the destination of the transport
     * @param time        the leaving time of the transport
     */
    public TransportDeparture(String category, String line, String destination, Date time) {
        this.category = category;
        this.line = line;
        this.destination = destination;
        this.time = time;
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
        return DateFormatters.TRANSPORT_TIME.format(time);
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
