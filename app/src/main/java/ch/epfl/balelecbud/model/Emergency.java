package ch.epfl.balelecbud.model;

import com.google.firebase.Timestamp;

import java.util.Objects;

/**
 * Class modeling an emergency posted by a user
 */
public final class Emergency {

    private EmergencyCategory category;
    private String message;
    private String userID;
    private Timestamp timestamp;

    /**
     * Empty constructor used by FireStore
     */
    public Emergency() { }

    /**
     * Constructor for emergencies
     *
     * @param category  the category of the emergency
     * @param message   the message linked with the emergency
     * @param userID    the ID of the user writing the emergency
     * @param timestamp the timestamp of the emergency
     */
    public Emergency(EmergencyCategory category, String message, String userID, Timestamp timestamp) {
        this.category = category;
        this.message = message;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public EmergencyCategory getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public String getUserID() {
        return userID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Emergency)
                && ((Emergency) o).category == category
                && Objects.equals(((Emergency) o).message, message)
                && Objects.equals(((Emergency) o).userID, userID)
                && Objects.equals(((Emergency) o).timestamp, timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getMessage(), getUserID(), getTimestamp());
    }
}
