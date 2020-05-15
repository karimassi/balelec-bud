package ch.epfl.balelecbud.model;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class User implements Parcelable {
    private static final String EMAIL = "User.EMAIL";
    private static final String DISPLAY_NAME = "User.DISPLAY_NAME";
    private static final String UID = "User.UID";

    private String email;
    private String displayName;
    private String uid;

    public User() { }

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

    public static boolean isAUserStored(SharedPreferences preferences) {
        return preferences.contains(EMAIL) &&
                preferences.contains(DISPLAY_NAME) &&
                preferences.contains(UID);
    }

    public void storeUser(SharedPreferences.Editor editor) {
        editor.putString(EMAIL, email);
        editor.putString(DISPLAY_NAME, displayName);
        editor.putString(UID, uid);
    }

    public static User restoreUser(SharedPreferences preferences) {
        if (isAUserStored(preferences)) {
            return new User(
                    preferences.getString(EMAIL, null),
                    preferences.getString(DISPLAY_NAME, null),
                    preferences.getString(UID, null)
            );
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof User)
                && Objects.equals(((User) o).getEmail(), email)
                && Objects.equals(((User) o).getDisplayName(), displayName)
                && Objects.equals(((User) o).getUid(), uid);
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
