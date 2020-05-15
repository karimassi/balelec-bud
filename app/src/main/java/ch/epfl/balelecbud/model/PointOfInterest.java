package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class PointOfInterest {
    private String name;
    private PointOfInterestType type;
    private Location location;
    private double radius;

    public PointOfInterest() {}

    public PointOfInterest(String name, PointOfInterestType type, Location location, double radius) {
        this.location = location;
        this.name = name;
        this.type = type;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public PointOfInterestType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof PointOfInterest)
                && Objects.equals(((PointOfInterest) obj).name, name)
                && Objects.equals(((PointOfInterest) obj).type, type)
                && Objects.equals(((PointOfInterest) obj).location, location)
                && ((PointOfInterest) obj).radius == radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, location, radius);
    }
}
