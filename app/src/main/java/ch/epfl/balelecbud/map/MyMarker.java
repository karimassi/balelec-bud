package ch.epfl.balelecbud.map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import ch.epfl.balelecbud.models.Location;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static java.util.Objects.requireNonNull;

public interface MyMarker {

    void setLocation(Location location);

    class Builder {

        private Location location;
        private String title;
        private MarkerType type;

        public Builder location(Location location) {
            this.location = requireNonNull(location);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(MarkerType type) {
            this.type = type;
            return this;
        }

        public Location getLocation() {
            return this.location;
        }

        public String getTitle() {
            return this.title;
        }

        public MarkerType getType() {
            return this.type;
        }

        MarkerOptions toMapboxMarkerOptions() {
            MarkerOptions result = new MarkerOptions();
            if (title != null) {
                result = result.title(title);
            }
            result = result.position(requireNonNull(location).toLatLng());
            if (type != null) {
                Drawable iconDrawable = ContextCompat.getDrawable(getAppContext(), type.getDrawableId());
                Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
                IconFactory iconFactory = IconFactory.getInstance(getAppContext());
                result = result.icon(iconFactory.fromBitmap(bitmap));
            }
            return result;
        }
    }
}
