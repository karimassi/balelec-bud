package ch.epfl.balelecbud.schedule.models;

import androidx.annotation.Nullable;

public class Slot {

    private String artistName;
    private String sceneName;
    private String timeSlot;

    public Slot(String artistName, String sceneName, String timeSlot) {
        this.artistName = artistName;
        this.timeSlot = timeSlot;
        this.sceneName = sceneName;
    }

    public Slot() {

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

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Slot)
                && ((Slot) obj).getArtistName() == artistName
                && ((Slot) obj).getTimeSlot() == timeSlot
                && ((Slot) obj).getSceneName() == sceneName;
    }
}
