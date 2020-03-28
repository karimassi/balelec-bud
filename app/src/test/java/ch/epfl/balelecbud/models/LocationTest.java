package ch.epfl.balelecbud.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LocationTest {

    private Location location = new Location(2,1);
    private Location sameLocation = new Location(2,1);
    private GeoPoint geoPoint = new GeoPoint(2, 1);
    private LatLng latLng = new LatLng(2, 1);

    @Test
    public void testEmptyConstructor() {
        new Location();
    }

    @Test
    public void testLocationFromGeoPoint() {
        assertThat(new Location(geoPoint), is(location));
    }

    @Test
    public void testToGeoPoint() {
        assertThat(location.toGeoPoint(), is(geoPoint));
    }

    @Test
    public void testLocationFromLatLng() {
        assertThat(new Location(latLng), is(location));
    }

    @Test
    public void testToLatLng() {
        assertThat(location.toLatLng(), is(latLng));
    }

    @Test
    public void testGetLatitude() {
        assertThat(location.getLatitude(), is(2.));
    }

    @Test
    public void testGetLongitude() {
        assertThat(location.getLongitude(), is(1.));
    }

    @Test
    public void testEqualsTwoEqualLocations() {
        assertEquals(location,sameLocation);
    }

    @Test
    public void testEqualsTwoNonEqualLocations() {
        Location differentLatitude = new Location(3,1);
        Location differentLongitude = new Location(2,3);
        Location allDifferent = new Location(3,3);

        assertFalse(location.equals(differentLatitude));
        assertFalse(location.equals(differentLongitude));
        assertFalse(location.equals(allDifferent));
    }


    @Test
    public void testEqualsTwoDifferentObjects() {
        assertFalse(location.equals(new Object()));
    }

    @Test
    public void testHashCode() {
        assertThat(sameLocation.hashCode(), is(location.hashCode()));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Location(lat = 2.0, long = 1.0)", location.toString());
    }
}

   