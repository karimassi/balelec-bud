package ch.epfl.balelecbud.model;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Class modeling a user
 */
public final class User implements Parcelable {

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
    private static final String EMAIL = "User.EMAIL";
    private static final String DISPLAY_NAME = "User.DISPLAY_NAME";
    private static final String UID = "User.UID";

    private String email;
    private String displayName;
    private String uid;

    /**
     * Empty constructor used by FireStore
     */
    public User() {
    }

    /**
     * Constructor for a user
     *
     * @param email       the email address of the user
     * @param displayName the name of the user
     * @param uid         the ID of the user
     */
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

    public static boolean isAUserStored(SharedPreferences preferences) {
        return preferences.contains(EMAIL) &&
                preferences.contains(DISPLAY_NAME) &&
                preferences.contains(UID);
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

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return uid;
    }

    public void storeUser(SharedPreferences.Editor editor) {
        editor.putString(EMAIL, email);
        editor.putString(DISPLAY_NAME, displayName);
        editor.putString(UID, uid);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof User)
                && Objects.equals(((User) o).email, email)
                && Objects.equals(((User) o).displayName, displayName)
                && Objects.equals(((User) o).uid, uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getDisplayName(), getUid());
    }

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
