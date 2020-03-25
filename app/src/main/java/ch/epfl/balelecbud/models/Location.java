package ch.epfl.balelecbud.models;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Location {

    private Double longitude;
    private Double latitude;

    public Location() {

    }

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Location) {
            Location that = (Location) obj;
            return this.latitude.equals(that.latitude) &&
                    this.longitude.equals(that.longitude);
        }
        return false;
    }
}
