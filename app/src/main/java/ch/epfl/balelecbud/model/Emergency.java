package ch.epfl.balelecbud.model;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class Emergency {

    private EmergencyType category;
    private String message;
    private String userID;
    private Timestamp timestamp;



    public Emergency(){

    }

    public Emergency(EmergencyType category,  String message, String userID, Timestamp timestamp){
        this.category = category;
        this.message=message;
        this.userID=userID;
        this.timestamp=timestamp;
    }

    public EmergencyType getCategory(){return category;}
    public String getMessage(){return message;}
    public String getUserID(){return userID;}
    public Timestamp getTimestamp(){return timestamp;}


    @Override
    public boolean equals(Object o) {
        return (o instanceof Emergency)
                && ((Emergency) o).getCategory().equals(category)
                && ((Emergency) o).getMessage().equals(message)
                && ((Emergency) o).getUserID().equals(userID)
                && ((Emergency) o).getTimestamp().equals(timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategory(), getMessage(), getUserID(), getTimestamp());
    }
}
