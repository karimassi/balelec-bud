package ch.epfl.balelecbud.pointOfInterest;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import ch.epfl.balelecbud.models.Location;

public class PointOfInterest {
    private String name;
    private PointOfInterestType type;
    private Location location;

    public PointOfInterest(){ }

    public PointOfInterest(Location location, String name, PointOfInterestType type) {
        this.location = location;
        this.name = name;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public PointOfInterestType getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof PointOfInterest)
                && ((PointOfInterest) obj).getName().equals(name)
                && ((PointOfInterest) obj).getType().equals(type)
                && ((PointOfInterest) obj).getLocation().equals(location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, location);
    }
}
