package ch.epfl.balelecbud.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.GeoPoint;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Objects;

public class Location implements Parcelable {

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
    public static Location DEFAULT_LOCATION = new Location(46.518802, 6.567550);
    private Double longitude;
    private Double latitude;
    private Double geoFireLocation;

    public Location() {
    }

    public Location(double latitude, double longitude, double geoFireLocation) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.geoFireLocation = geoFireLocation;
    }

    public Location(double latitude, double longitude) {
        this(latitude, longitude, (latitude + 90) * 180 + longitude);
    }

    protected Location(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        geoFireLocation = in.readDouble();
    }

    public Location(GeoPoint geoPoint) {
        this(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    public Location(LatLng latLng) {
        this(latLng.getLatitude(), latLng.getLongitude());
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Double getGeoFireLocation() {
        return geoFireLocation;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
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
    }

}