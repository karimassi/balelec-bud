package ch.epfl.balelecbud.view.map;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import org.junit.Test;

import ch.epfl.balelecbud.model.Location;

public class MapboxMarkerAdapterTest {
    @Test
    public void mapboxMarkerAdapterWithNullMarker() {
        // check that no error is thrown
        new MapboxMarkerAdapter(null);
    }

    @Test
    public void mapboxMarkerAdapterWithNonNullMarker() {
        new MapboxMarkerAdapter(new Marker(new MarkerOptions()));
    }

    @Test(expected = NullPointerException.class)
    public void setLocationWithNonNullLocationNullMarker() {
        new MapboxMarkerAdapter(null).setLocation(new Location(1, 2));
    }

    @Test
    public void setLocationWithNonNullLocationNonNullMarker() {
        // check that no error is thrown
        new MapboxMarkerAdapter(new Marker(new MarkerOptions())).setLocation(new Location(1, 2));
    }

    @Test
    public void setLocationWithNullLocation() {
        // check that no error is thrown
        new MapboxMarkerAdapter(null).setLocation(null);
    }
}