package ch.epfl.balelecbud.gallery;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

public class Picture implements Parcelable {
    @PrimaryKey
    private int id;

    private String imageFileName;

    public Picture(int id, String imageFileName) {
        this.id = id;
        this.imageFileName = imageFileName;
    }

    protected Picture(Parcel in) {
        id = in.readInt();
        imageFileName = in.readString();
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getImageFileName() { return imageFileName; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Picture)
                && ((Picture) obj).id == id
                && ((Picture) obj).getImageFileName().equals(imageFileName);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imageFileName);
    }
}
