package ch.epfl.balelecbud.map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.models.Location;

@RunWith(JUnit4.class)
public class MapboxMapAdapterTest {

    @Test
    public void mapboxMapAdapterWithNullMapDoesNotThrowNPE() {
        // check that no error is thrown
        new MapboxMapAdapter(null);
    }

    @Test(expected = NullPointerException.class)
    public void addMarkerWithNullMapThrowsNPE() {
        new MapboxMapAdapter(null).addMarker(new MyMarker.Builder().location(new Location(1, 2)));
    }

    @Test
    public void addNullMarkerWithNullMapDoesNotThrowsNPE() {
        // check that no error is thrown
        new MapboxMapAdapter(null).addMarker(null);
    }

    @Test(expected = NullPointerException.class)
    public void initialiseMapWithLocationWithNullMapThrowsNPE() {
        new MapboxMapAdapter(null).initialiseMap(true);
    }

    @Test(expected = NullPointerException.class)
    public void initialiseMapWithoutLocationWithNullMapThrowsNPE() {
        new MapboxMapAdapter(null).initialiseMap(false);
    }
}