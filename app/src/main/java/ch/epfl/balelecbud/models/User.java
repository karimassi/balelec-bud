package ch.epfl.balelecbud.models;

public class User {

    private String email;
    private String displayName;
    private String userToken;

    public User() {

    }

    public User(String email, String displayName, String userToken) {
        this.email = email;
        this.displayName = displayName;
        this.userToken = userToken;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return userToken;
    }

}
