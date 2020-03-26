package ch.epfl.balelecbud.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class User implements Parcelable {

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

    protected User(Parcel in) {
        email = in.readString();
        displayName = in.readString();
        uid = in.readString();
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

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(displayName);
        dest.writeString(uid);
    }
}
