package ch.epfl.balelecbud.map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.models.Location;

@RunWith(JUnit4.class)
public class GoogleMapAdapterTest {
    @Test
    public void googleMapAdapterWithNullMapDoesNotThrowNPE() {
        // check that no error is thrown
        new GoogleMapAdapter(null);
    }

    @Test(expected = NullPointerException.class)
    public void enableMyLocationWithNullMapThrowsNPE() {
        new GoogleMapAdapter(null).enableUserLocation(false);
    }

    @Test(expected = NullPointerException.class)
    public void disableMyLocationWithNullMapThrowsNPE() {
        new GoogleMapAdapter(null).enableUserLocation(false);
    }

    @Test(expected = NullPointerException.class)
    public void addMarkerWithNullMapThrowsNPE() {
        new GoogleMapAdapter(null).addMarker(new MyMarker.Builder().location(new Location(1, 2)));
    }

    @Test
    public void addNullMarkerWithNullMapDoesNotThrowsNPE() {
        // check that no error is thrown
        new GoogleMapAdapter(null).addMarker(null);
    }
}