package ch.epfl.balelecbud.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

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
        return (o instanceof User)
                && ((User) o).getEmail() == email
                && ((User) o).getDisplayName() == displayName
                && ((User) o).getUid() == uid;

    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getDisplayName(), getUid());
    }
}
