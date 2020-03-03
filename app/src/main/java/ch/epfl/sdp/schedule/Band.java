package ch.epfl.sdp.schedule;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.Optional;

import ch.epfl.sdp.utils.Preconditions;

public class Band {

    private final String name;
    private final Optional<String> description;
    private final Optional<Bitmap> picture;

    public Band(String name, Optional<String> description, Optional<Bitmap> picture) {
        Preconditions.checkNonNull(name);
        Preconditions.checkNonNull(description);
        Preconditions.checkNonNull(picture);
        Preconditions.checkArgument(!name.isEmpty(), "Name cannot be empty");
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<Bitmap> getPicture() {
        return picture;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Band)){
            return false;
        }
        Band other = (Band) obj;
        return name.equals(other.name) &&
               description.equals(other.description) &&
               picture.equals(other.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, picture);
    }

    static class Id {

        private final int id;

        public Id(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
