package ch.epfl.balelecbud.models;

import androidx.annotation.Nullable;
import com.google.firebase.firestore.GeoPoint;

public class PointOfInterest {
    private String name;
    private String type;
    private String poiToken;
    private GeoPoint location;

    public PointOfInterest(){ }


    public PointOfInterest(GeoPoint location, String name, String type, String poiToken) {
        this.location = location;
        this.name = name;
        this.type = type;
        this.poiToken = poiToken;
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

    public String getPoiToken() {
        return poiToken;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof PointOfInterest)
                && ((PointOfInterest) obj).getName() == name
                && ((PointOfInterest) obj).getType() == type
                && ((PointOfInterest) obj).getLocation().equals(location)
                && ((PointOfInterest) obj).getPoiToken() == poiToken;
    }
}
