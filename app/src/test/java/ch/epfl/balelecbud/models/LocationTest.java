package ch.epfl.balelecbud.models;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
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
        assertEquals(location1, same_as_l1);
    }

    @Test
    public void testEqualsTwoNonEqualLocation() {
        Assert.assertNotEquals(location1, location2);
    }


    @Test
    public void testEqualsTwoDifferentObjects() {
        Assert.assertNotEquals(location1, new Object());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Location(lat = 2.0, long = 1.0)", location1.toString());
    }

}