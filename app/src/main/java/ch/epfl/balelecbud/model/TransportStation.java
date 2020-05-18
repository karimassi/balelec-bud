package ch.epfl.balelecbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Class modeling a public transport station
 */
public final class TransportStation implements Parcelable {

    public static final Creator<TransportStation> CREATOR = new Creator<TransportStation>() {
        @Override
        public TransportStation createFromParcel(Parcel in) {
            return new TransportStation(in);
        }

        @Override
        public TransportStation[] newArray(int size) {
            return new TransportStation[size];
        }
    };

    private Location location;
    private String stationId;
    private String stationName;
    private double distanceToUser;

    /**
     * Constructor for a transport station
     *
     * @param location       the location of the station
     * @param stationId      the id of the station
     * @param stationName    the name of the station
     * @param distanceToUser the distance from the user to the station
     */
    public TransportStation(Location location, String stationId, String stationName, double distanceToUser) {
        this.location = location;
        this.stationId = stationId;
        this.stationName = stationName;
        this.distanceToUser = distanceToUser;
    }

    protected TransportStation(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        stationId = in.readString();
        stationName = in.readString();
        distanceToUser = in.readDouble();
    }

    public Location getLocation() {
        return location;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportStation that = (TransportStation) o;
        return Double.compare(that.getDistanceToUser(), getDistanceToUser()) == 0 &&
                Objects.equals(getLocation(), that.getLocation()) &&
                Objects.equals(getStationId(), that.getStationId()) &&
                Objects.equals(getStationName(), that.getStationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getStationId(), getStationName(), getDistanceToUser());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(stationId);
        dest.writeString(stationName);
        dest.writeDouble(distanceToUser);
    }
}
