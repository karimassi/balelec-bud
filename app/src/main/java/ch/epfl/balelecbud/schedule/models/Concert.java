package ch.epfl.balelecbud.schedule.models;

public class Concert {

    public String name;
    public String description;
    public String timeSlot;
    public String uid;

    public Concert() {
        // Default constructor required for calls to DataSnapshot.getValue(Concert.class)
    }

    public Concert(String name, String description, String timeslot, String uid) {
        this.name = name;
        this.description = description;
        this.timeSlot = timeslot;
        this.uid = uid;
    }


}
