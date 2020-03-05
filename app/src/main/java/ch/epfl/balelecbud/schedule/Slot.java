package ch.epfl.balelecbud.schedule;

import java.util.ArrayList;

public class Slot {
    private String artistName;
    private String timeSlot;
    private String sceneName;

    public Slot(String artistName, String timeSlot, String sceneName) {
        this.artistName = artistName;
        this.timeSlot = timeSlot;
        this.sceneName = sceneName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getSceneName() {
        return sceneName;
    }

    private static int lastContactId = 0;

    public static ArrayList<Slot> createSchedule(int numSlots) {
        ArrayList<Slot> schedule = new ArrayList<Slot>();
        for (int i = 1; i <= numSlots; i++) {
            schedule.add(new Slot("oui", "oui", "oui"));
        }
        return schedule;
    }
}
