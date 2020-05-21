package ch.epfl.balelecbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Objects;

/**
 * Class modeling a Location
 */
public final class Location implements Parcelable {

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    /**
     * The default location at which the map opens
     */
    public static final Location DEFAULT_LOCATION = new Location(46.518802, 6.567550);
    private Double longitude;
    private Double latitude;
    private Double geoFireLocation;
    private Long timestamp;

    /**
     * Empty constructor used by FireStore
     */
    public Location() { }


    /**
     * Constructor for location
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public Location(double latitude, double longitude) {
        this(latitude, longitude, false);
    }

    /**
     * Constructor for location
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param createTimestamp if a timestamp should be created or instantiated to 1
     */
    public Location(double latitude, double longitude, boolean createTimestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.geoFireLocation = (latitude + 90) * 180 + longitude;
        this.timestamp = createTimestamp ? System.currentTimeMillis() : -1;
    }

    protected Location(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        geoFireLocation = in.readDouble();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getGeoFireLocation() {
        return geoFireLocation;
    }

    public long getTimestamp() {return timestamp; }

    /**
     * Convert the location into Mapbox's {@code LatLng}
     *
     * @return the converted location
     */
    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        //timestamp intentionally not considered
        if (obj instanceof Location) {
            Location that = (Location) obj;
            return this.latitude.equals(that.latitude)
                    && this.longitude.equals(that.longitude);
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "Location(lat = " + latitude.toString() + ", long = " + longitude.toString() + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(geoFireLocation);
        dest.writeLong(timestamp);
    }
}