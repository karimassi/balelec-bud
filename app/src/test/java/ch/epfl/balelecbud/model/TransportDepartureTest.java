package ch.epfl.balelecbud.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import ch.epfl.balelecbud.utility.DateFormatters;

public class TransportDepartureTest {
    private TransportDeparture t1 = new TransportDeparture("M", "m1", "Flon", new Date(Long.parseLong("1585866499")*1000));

    @Test
    public void testGetCategory() {
        Assert.assertEquals("M", t1.getCategory());
    }

    @Test
    public void testGetLine() {
        Assert.assertEquals("m1", t1.getLine());
    }

    @Test
    public void testGetDestination() {
        Assert.assertEquals("Flon", t1.getDestination());
    }

    @Test
    public void testGetTimeString() {
        Assert.assertEquals(DateFormatters.TRANSPORT_TIME.format(new Date(Long.parseLong("1585866499")*1000)), t1.getTimeString());
    }

    @Test
    public void testGetTime() {
        Assert.assertEquals(new Date(Long.parseLong("1585866499")*1000), t1.getTime());
    }

    @Test
    public void testEquals() {
        TransportDeparture other = new TransportDeparture("B", "m1", "Flon", new Date(Long.parseLong("1585866499")*1000));
        Assert.assertNotEquals(other, t1);

        other = new TransportDeparture("M", "m2", "Flon", new Date(Long.parseLong("1585866499")*1000));
        Assert.assertNotEquals(other, t1);

        other = new TransportDeparture("M", "m1", "Renens", new Date(Long.parseLong("1585866499")*1000));
        Assert.assertNotEquals(other, t1);

        other = new TransportDeparture("M", "m1", "Flon", new Date(Long.parseLong("1585866600")*1000));
        Assert.assertNotEquals(other, t1);

        Assert.assertEquals(t1, t1);
        Assert.assertNotEquals(t1, new Object());


        other = new TransportDeparture("M", "m1", "Flon", new Date(Long.parseLong("1585866499")*1000));
        Assert.assertEquals(t1, other);
        Assert.assertEquals(t1.hashCode(), other.hashCode());
    }


}
