package ch.epfl.balelecbud.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppContext;
import static java.util.Objects.requireNonNull;

/**
 * Interface modeling marker on the map
 */
public interface MyMarker {

    /**
     * Change the marker's current location
     *
     * @param location the new location of the marker
     */
    void setLocation(Location location);

    /**
     * Builder for a marker
     */
    class Builder {

        private Location location;
        private String title;
        private MarkerType type;
        private String snippet;

        /**
         * Set the location for the marker under construction
         *
         * @param location the location of the marker
         * @return         this builder
         * @throws         NullPointerException if the given location is {@code null}
         */
        public Builder location(Location location) {
            this.location = requireNonNull(location);
            return this;
        }

        /**
         * Set the title of the marker under construction
         *
         * @param title the title of the marker
         * @return      this builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the type, and so the icon, of the marker under construction
         *
         * @param type the type of the marker
         * @return     this builder
         */
        public Builder type(MarkerType type) {
            this.type = type;
            return this;
        }

        /**
         * Set the snippet of the marker under construction
         *
         * @param snippet the snippet
         * @return        this builder
         */
        public Builder snippet(String snippet) {
            this.snippet = snippet;
            return this;
        }

        /**
         * Return the location of the marker under construction
         *
         * @return the location or {@code null} if it has not been set yet
         */
        public Location getLocation() {
            return this.location;
        }

        /**
         * Return the title of the marker under construction
         *
         * @return the title or {@code null} if it has not been set yet
         */
        public String getTitle() {
            return this.title;
        }

        /**
         * Return the type of the marker under construction
         *
         * @return the type or {@code null} if it has not been set yet
         */
        public MarkerType getType() {
            return this.type;
        }

        /**
         * Return the snippet of the marker under construction
         *
         * @return the snippet or {@code null} if it has not been set yet
         */
        public String getSnippet() {
            return this.snippet;
        }

        /**
         * Transform this builder into a {@code MapboxMarkerOption}
         *
         * @return this converted builder
         * @throws NullPointerException if the location of the marker has not been set
         */
        public MarkerOptions toMapboxMarkerOptions() {
            MarkerOptions result = new MarkerOptions();
            if (title != null) {
                result.title(title);
            }
            result.position(requireNonNull(location).toLatLng());
            if (type != null) {

                Drawable iconDrawable = ContextCompat.getDrawable(getAppContext(), type.getDrawableId());
                Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
                IconFactory iconFactory = IconFactory.getInstance(getAppContext());
                result.icon(iconFactory.fromBitmap(bitmap));
            }
            if (snippet != null) {
                result.setSnippet(snippet);
            }
            return result;
        }
    }
}
