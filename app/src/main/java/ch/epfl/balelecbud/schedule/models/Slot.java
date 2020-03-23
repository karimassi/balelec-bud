package ch.epfl.balelecbud.schedule.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import java.io.Serializable;

import ch.epfl.balelecbud.util.StringUtils;

@Entity
public class Slot implements Serializable {
    @PrimaryKey
    private int id;

    private String artistName;
    private String sceneName;
    private Timestamp startTime;
    private Timestamp endTime;

    public Slot(int id, String artistName, String sceneName, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.artistName = artistName;
        this.sceneName = sceneName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Ignore
    public Slot() {

    }

    public int getId() {
        return id;
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
                && ((Slot) obj).id == id
                && ((Slot) obj).getArtistName().equals(artistName)
                && ((Slot) obj).getStartTime().equals(startTime)
                && ((Slot) obj).getEndTime().equals(endTime)
                && ((Slot) obj).getSceneName().equals(sceneName);
    }
}
