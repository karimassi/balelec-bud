package ch.epfl.balelecbud.schedule.models;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import ch.epfl.balelecbud.util.StringUtils;

public class Slot {

    private String artistName;
    private String sceneName;
    private Timestamp startTime;
    private Timestamp endTime;

    public Slot(String artistName, String sceneName, Timestamp startTime, Timestamp endTime) {
        this.artistName = artistName;
        this.sceneName = sceneName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Slot(){

    }

    public String getArtistName() {
        return artistName;
    }

    public Timestamp getStartTime() { return startTime; }

    public Timestamp getEndTime() { return endTime; }

    public String getSceneName() {
        return sceneName;
    }

    public String getTimeSlot(){
        return StringUtils.timestampToScheduleString(startTime) + " - " +
                StringUtils.timestampToScheduleString(endTime);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Slot)
                && ((Slot)obj).getArtistName() == artistName
                && ((Slot) obj).getStartTime() == startTime
                && ((Slot) obj).getEndTime() == endTime
                && ((Slot) obj).getSceneName() == sceneName;
    }
}
