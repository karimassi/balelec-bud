package ch.epfl.balelecbud.models;

import com.google.firebase.firestore.GeoPoint;

public class PointOfInterest {
    private String name;
    private String type;
    private String poiToken;
    private GeoPoint location;
    

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
}
