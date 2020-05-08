package ch.epfl.balelecbud.schedule.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import ch.epfl.balelecbud.util.StringUtils;

@Entity
public class Slot implements Parcelable {
    @PrimaryKey
    private int id;

    private String artistName;
    private String sceneName;
    private String imageFileName;
    private Timestamp startTime;
    private Timestamp endTime;

    public Slot(int id, String artistName, String sceneName, String imageFileName, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.artistName = artistName;
        this.sceneName = sceneName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageFileName = imageFileName;
    }

    @Ignore
    public Slot() { }

    protected Slot(Parcel in) {
        id = in.readInt();
        artistName = in.readString();
        sceneName = in.readString();
        imageFileName = in.readString();
        startTime = in.readParcelable(Timestamp.class.getClassLoader());
        endTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Slot> CREATOR = new Creator<Slot>() {
        @Override
        public Slot createFromParcel(Parcel in) {
            return new Slot(in);
        }

        @Override
        public Slot[] newArray(int size) {
            return new Slot[size];
        }
    };

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

    public String getImageFileName() {
        return imageFileName;
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
                && ((Slot) obj).getSceneName().equals(sceneName)
                && ((Slot) obj).getImageFileName().equals(imageFileName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(artistName);
        dest.writeString(sceneName);
        dest.writeString(imageFileName);
        dest.writeParcelable(startTime, flags);
        dest.writeParcelable(endTime, flags);
    }
}
