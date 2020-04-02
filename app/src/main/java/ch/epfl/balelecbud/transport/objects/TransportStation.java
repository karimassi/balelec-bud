package ch.epfl.balelecbud.transport.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;

public class TransportStation implements Parcelable {

    private Location location;
    private String stationId;
    private String stationName;
    private double distanceToUser;

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
