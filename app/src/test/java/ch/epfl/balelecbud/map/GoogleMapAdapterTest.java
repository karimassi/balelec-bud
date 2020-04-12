package ch.epfl.balelecbud.map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GoogleMapAdapterTest {
    @Test
    public void googleMapAdapterWithNullMap() {
        // check that no error is thrown
        new GoogleMapAdapter(null);
    }

    @Test(expected = NullPointerException.class)
    public void setMyLocationEnabledWithNullMapThrowsNPE() {
        new GoogleMapAdapter(null).setMyLocationEnabled(false);
    }

    @Test(expected = NullPointerException.class)
    public void addMarkerWithNullMapThrowsNPE() {
        new GoogleMapAdapter(null).addMarker(new MyMarker.Builder());
    }
}