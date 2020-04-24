package ch.epfl.balelecbud.map;


import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import ch.epfl.balelecbud.models.Location;

import static java.util.Objects.requireNonNull;

public interface MyMarker {

    void setLocation(Location location);

    class Builder {

        private Location location;
        private String title;
        private Icon icon;

        public Builder location(Location location) {
            this.location = requireNonNull(location);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder icon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public Location getLocation() {
            return this.location;
        }

        public String getTitle() {
            return this.title;
        }

        public Icon getIcon() {
            return icon;
        }

        MarkerOptions toMapboxMarkerOptions() {
            MarkerOptions result = new MarkerOptions();
            if (title != null) {
                result = result.title(title);
            }
            result = result.position(requireNonNull(location).toLatLng());
            if (icon != null) {
                result = result.icon(icon);
            }
            return result;
        }


    }
}
