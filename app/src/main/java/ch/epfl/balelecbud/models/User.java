package ch.epfl.balelecbud.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

public class User {

    private String email;
    private GeoPoint location;
    private String displayName;
    private String userToken;

    public User() {

    }

    public User(String email, GeoPoint location, String displayName, String userToken) {
        this.email = email;
        this.location = location;
        this.displayName = displayName;
        this.userToken = userToken;
    }

    public String getEmail() {
        return email;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserToken() {
        return userToken;
    }

}
