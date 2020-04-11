package ch.epfl.balelecbud.map;

import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.models.Location;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MyMarkerBuilderTest {
    private final Location location = new Location(1, 2);
    private final String title = "abcd";

    @Test
    public void canSetLocation() {
        assertEquals(location, new MyMarker.Builder().location(location).getLocation());
    }

    @Test
    public void canSetTitle() {
        assertEquals(title, new MyMarker.Builder().title(title).getTitle());
    }

    @Test
    public void canConvertToGoogleMarkerOption() {
        MarkerOptions markerOptions =
                new MyMarker.Builder().
                        title(title).
                        location(location).
                        toGoogleMarkerOptions();
        assertEquals(title, markerOptions.getTitle());
        assertEquals(location.toLatLng(), markerOptions.getPosition());
    }
}
