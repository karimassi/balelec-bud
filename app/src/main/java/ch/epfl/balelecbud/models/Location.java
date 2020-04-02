package ch.epfl.balelecbud.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

public class Location implements Parcelable {

    private Double longitude;
    private Double latitude;

    public static Location DEFAULT_LOCATION = new Location(46.518802, 6.567550);

    public Location() {}

    public Location(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected Location(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public Location(android.location.Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }

    public Location(GeoPoint geoPoint) {
        this.latitude = geoPoint.getLatitude();
        this.longitude = geoPoint.getLongitude();
    }

    public Location(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(latitude, longitude);
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

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

}