package ch.epfl.balelecbud.map;

import org.junit.Test;

import ch.epfl.balelecbud.models.Location;

import static org.junit.Assert.*;

public class GoogleMarkerAdapterTest {
    @Test
    public void googleMarkerAdapterWithNullMarker() {
        // check that no error is thrown
        new GoogleMarkerAdapter(null);
    }

    @Test(expected = NullPointerException.class)
    public void setLocationWithNonNullLocation() {
        new GoogleMarkerAdapter(null).setLocation(new Location(1, 2));
    }

    @Test
    public void setLocationWithNullLocation() {
        // check that no error is thrown
        new GoogleMarkerAdapter(null).setLocation(null);
    }
}