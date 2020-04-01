package ch.epfl.balelecbud.models.emergency;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;


import java.util.Objects;

public class Emergency {

    private EmergencyType category;
    private GeoPoint location;
    private String message;
    private String userID;
    private Timestamp timestamp;



    public Emergency(){

    }

    public Emergency(EmergencyType category, GeoPoint location, String message, String userID, Timestamp timestamp){
        this.category = category;
        this.location=location;
        this.message=message;
        this.userID=userID;
        this.timestamp=timestamp;
    }

    public EmergencyType getCategory(){return category;}
    public String getMessage(){return message;}
    public String getUserID(){return userID;}
    public GeoPoint getLocation(){return location;}
    public Timestamp getTimestamp(){return timestamp;}
    public String getCategoryString() {
        return category.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Emergency)
                && ((Emergency) o).getCategory().equals(category)
                && ((Emergency) o).getMessage().equals(message)
                && ((Emergency) o).getUserID().equals(userID)
                && ((Emergency) o).getLocation().equals(location)
                && ((Emergency) o).getTimestamp().equals(timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getMessage(), getUserID(), getLocation(), getTimestamp());
    }
}
