package ch.epfl.balelecbud.map;

import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.balelecbud.models.Location;

public interface MyMarker {
    void setLocation(Location location);

    class Builder {
        private Location location;
        private String title;

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Location getLocation() {
            return this.location;
        }

        public String getTitle() {
            return this.title;
        }

        MarkerOptions toGoogleMarkerOptions() {
            return new MarkerOptions().title(this.title).position(this.location.toLatLng());
        }
    }
}
