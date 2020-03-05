package ch.epfl.balelecbud.schedule.models;

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

}
