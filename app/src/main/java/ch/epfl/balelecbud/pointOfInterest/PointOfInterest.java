package ch.epfl.balelecbud.pointOfInterest;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.GeoPoint;

public class PointOfInterest {
    private String name;
    private String type;
    private GeoPoint location;

    public PointOfInterest(){ }

    public PointOfInterest(GeoPoint location, String name, String type) {
        this.location = location;
        this.name = name;
        this.type = type;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof PointOfInterest)
                && ((PointOfInterest) obj).getName().equals(name)
                && ((PointOfInterest) obj).getType().equals(type)
                && ((PointOfInterest) obj).getLocation().equals(location);
    }
}
