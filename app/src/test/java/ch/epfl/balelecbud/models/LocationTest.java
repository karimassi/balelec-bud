package ch.epfl.balelecbud.models;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.core.Is.is;

public class LocationTest {

    private Location location1 = new Location(1,2);
    private Location location2 = new Location(2,2);
    private Location same_as_l1 = new Location(1,2);



    @Test
    public void testEmptyConstructor() {
        new Location();
    }

    @Test
    public void testGetLatitude() {
        Assert.assertThat(location1.getLatitude(), is(2.));
    }

    @Test
    public void testGetLongitude() {
        Assert.assertThat(location1.getLongitude(), is(1.));
    }

    @Test
    public void testEqualsTwoEqualLocations() {
        assertEquals(location1,same_as_l1);
    }

    @Test
    public void testEqualsTwoNonEqualLocation() {
        assertFalse(location1.equals(location2));
    }


    @Test
    public void testEqualsTwoDifferentObjects() {
        assertFalse(location1.equals(new Object()));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Location(lat = 2.0, long = 1.0)", location1.toString());
    }
}

   