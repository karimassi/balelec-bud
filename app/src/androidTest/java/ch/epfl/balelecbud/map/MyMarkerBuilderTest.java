package ch.epfl.balelecbud.map;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.models.Location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
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
    public void canSetIcon() {
        assertNull(new MyMarker.Builder().icon(null).getIcon());
    }

    @Test
    public void canConvertToMapboxMarkerOption() {
        MarkerOptions markerOptions = new MyMarker.Builder().
                title(title).location(location).icon(null).
                toMapboxMarkerOptions();
        assertEquals(title, markerOptions.getTitle());
        assertEquals(location.toLatLng(), markerOptions.getPosition());

        markerOptions = new MyMarker.Builder().
                location(location).
                toMapboxMarkerOptions();
        assertEquals(location.toLatLng(), markerOptions.getPosition());

        markerOptions = new MyMarker.Builder().
                title(title).
                location(location).
                toMapboxMarkerOptions();
        assertEquals(title, markerOptions.getTitle());

        markerOptions = new MyMarker.Builder().
                title(null).location(location).icon(null).
                toMapboxMarkerOptions();
        assertEquals(markerOptions, new MarkerOptions().setPosition(location.toLatLng()));
    }

    @Test(expected = NullPointerException.class)
    public void markerCannotHaveNullLocation() {
        new MyMarker.Builder()
                .location(null);
    }

    @Test(expected = NullPointerException.class)
    public void markerCannotHaveUnInitializedLocation() {
        new MyMarker.Builder().toMapboxMarkerOptions();
    }
}
