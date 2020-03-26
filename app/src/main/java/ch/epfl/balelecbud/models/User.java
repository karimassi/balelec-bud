package ch.epfl.balelecbud.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getEmail().equals(user.getEmail()) &&
                getDisplayName().equals(user.getDisplayName()) &&
                getUid().equals(user.getUid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getDisplayName(), getUid());
    }
}
