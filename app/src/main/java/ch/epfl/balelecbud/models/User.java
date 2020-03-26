package ch.epfl.balelecbud.models;

public class User {

    private String email;
    private String displayName;
    private String uid;

    public User() {

    }

    public User(String email, String displayName, String uid) {
        this.email = email;
        this.displayName = displayName;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return uid;
    }

}
