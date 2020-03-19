package ch.epfl.balelecbud.models;

import com.google.firebase.firestore.GeoPoint;

public class User {
    private String email;
    private GeoPoint location;
    private String name;
    private String surname;
    private String userToken;


    public User() {
    }

    public User(String email, GeoPoint location, String name, String surname, String userToken) {
        this.email = email;
        this.location = location;
        this.name = name;
        this.surname = surname;
        this.userToken = userToken;
    }

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

    public String getUserToken() {
        return userToken;
    }
}
