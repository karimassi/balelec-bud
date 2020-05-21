package ch.epfl.balelecbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import java.util.Objects;

import ch.epfl.balelecbud.utility.DateFormatters;

/**
 * Class modeling a concert slot
 */
@Entity
public final class Slot implements Parcelable {

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

    @PrimaryKey
    private int id;
    private String artistName;
    private String sceneName;
    private String imageFileName;
    private Timestamp startTime;
    private Timestamp endTime;

    /**
     * Constructor for a concert slot
     *
     * @param id            the id of the concert (should be unique)
     * @param artistName    the artist's name
     * @param sceneName     the name of the scene
     * @param imageFileName the path of the image to display
     * @param startTime     the starting time of the concert
     * @param endTime       the ending time of the concert
     */
    public Slot(int id, String artistName, String sceneName, String imageFileName, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.artistName = artistName;
        this.sceneName = sceneName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageFileName = imageFileName;
    }

    /**
     * Empty constructor used by FireStore
     */
    @Ignore
    public Slot() {
    }

    protected Slot(Parcel in) {
        id = in.readInt();
        artistName = in.readString();
        sceneName = in.readString();
        imageFileName = in.readString();
        startTime = in.readParcelable(Timestamp.class.getClassLoader());
        endTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public String getArtistName() {
        return artistName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getSceneName() {
        return sceneName;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public String getTimeSlot() {
        return DateFormatters.IN_DAY.format(startTime) + " - " +
                DateFormatters.IN_DAY.format(endTime);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Slot)
                && ((Slot) obj).id == id
                && Objects.equals(((Slot) obj).artistName, artistName)
                && Objects.equals(((Slot) obj).startTime, startTime)
                && Objects.equals(((Slot) obj).endTime, endTime)
                && Objects.equals(((Slot) obj).sceneName, sceneName)
                && Objects.equals(((Slot) obj).imageFileName, imageFileName);
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
