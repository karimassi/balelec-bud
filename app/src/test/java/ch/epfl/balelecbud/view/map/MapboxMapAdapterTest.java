package ch.epfl.balelecbud.view.map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.MyMap;
import ch.epfl.balelecbud.model.MyMarker;

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
        new MapboxMapAdapter(null).initialiseMap(true, new Location(), MyMap.DEFAULT_ZOOM);
    }

    @Test(expected = NullPointerException.class)
    public void initialiseMapWithoutLocationWithNullMapThrowsNPE() {
        new MapboxMapAdapter(null).initialiseMap(false, new Location(), MyMap.DEFAULT_ZOOM);
    }
}