package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Class modeling the points of interest of the festival
 */
public final class PointOfInterest {

    private String name;
    private PointOfInterestType type;
    private Location location;
    private double radius;
    private Integer affluence;

    /**
     * Empty constructor used by FireStore
     */
    public PointOfInterest() {}

    /**
     * Constructor for the points of interest
     *
     * @param name      the name of the poi
     * @param type      the type of the poi
     * @param location  the location of the poi
     * @param radius    the radius used to count the number of person at this poi
     */
    public PointOfInterest(String name, PointOfInterestType type, Location location, double radius) {
        this(name, type, location, radius, null);
    }

    /**
     * Constructor for the points of interest
     *
     * @param name      the name of the poi
     * @param type      the type of the poi
     * @param location  the location of the poi
     * @param radius    the radius used to count the number of person at this poi
     * @param affluence the recorded affluence of the poi
     */
    public PointOfInterest(String name, PointOfInterestType type, Location location, double radius, Integer affluence) {
        this.location = location;
        this.name = name;
        this.type = type;
        this.radius = radius;
        this.affluence = affluence;
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

    public Integer getAffluence() {return affluence; }

    // affluence purposely not used, reasoning is that we do not want the affluence to affect the
    // hash value of the class and when taking this into account it does not make sense to check for
    // it during equality
    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof PointOfInterest)
                && Objects.equals(((PointOfInterest) obj).name, name)
                && Objects.equals(((PointOfInterest) obj).type, type)
                && Objects.equals(((PointOfInterest) obj).location, location)
                && ((PointOfInterest) obj).radius == radius;
    }

    // affluence purposely not used, we want the object to hash to the same value whether it's
    // affluence is computed so that it remains in the same place in the cache
    @Override
    public int hashCode() {
        return Objects.hash(name, type, location, radius);
    }
}
