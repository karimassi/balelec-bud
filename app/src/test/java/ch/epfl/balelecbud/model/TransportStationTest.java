package ch.epfl.balelecbud.model;

import org.junit.Assert;
import org.junit.Test;

public class TransportStationTest {

    private TransportStation station = new TransportStation(Location.DEFAULT_LOCATION, "0", "Flon", 10);

    @Test
    public void testGetLocation() {
        Assert.assertEquals(Location.DEFAULT_LOCATION, station.getLocation());
    }

    @Test
    public void testGetSationId() {
        Assert.assertEquals("0", station.getStationId());
    }

    @Test
    public void testGetStationName() {
        Assert.assertEquals("Flon", station.getStationName());
    }

    @Test
    public void testGetDistance() {
        Assert.assertEquals(10, station.getDistanceToUser(), 0);
    }

    @Test
    public void testGetFormattedDistance() {
        Assert.assertEquals("10 m.", station.getFormattedDistanceToUser(), 0);
    }

    @Test
    public void testEquals() {
        TransportStation other = new TransportStation(new Location(1, 2), "0", "Flon", 10);
        Assert.assertNotEquals(other, station);

        other = new TransportStation(Location.DEFAULT_LOCATION, "1", "Flon", 10);
        Assert.assertNotEquals(other, station);

        other = new TransportStation(Location.DEFAULT_LOCATION, "0", "Geneve", 10);
        Assert.assertNotEquals(other, station);

        other = new TransportStation(Location.DEFAULT_LOCATION, "0", "Flon", 120);
        Assert.assertNotEquals(other, station);

        other = new TransportStation(Location.DEFAULT_LOCATION, "0", "Flon", 10);
        Assert.assertEquals(station, other);
        Assert.assertEquals(station, station);
        Assert.assertNotEquals(station, new Object());
        Assert.assertEquals(other.hashCode(), station.hashCode());

    }




}
