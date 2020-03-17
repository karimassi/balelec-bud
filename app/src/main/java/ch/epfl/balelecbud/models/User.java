package ch.epfl.balelecbud.models;

import com.google.firebase.firestore.GeoPoint;

public class User {
    private String email;
    private GeoPoint location;
    private String name;
    private String surname;


    public User() {}

    public String getEmail() {
        return email;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
