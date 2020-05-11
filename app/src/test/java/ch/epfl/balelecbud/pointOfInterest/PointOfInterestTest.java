package ch.epfl.balelecbud.pointOfInterest;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.models.Location;

import static org.hamcrest.core.Is.is;

public class PointOfInterestTest {

    private final PointOfInterest p1 =
            new PointOfInterest("credit suisse", PointOfInterestType.ATM,
                    new Location(24, 42), 0.003);

    @Test
    public void testEmptyConstructor() {
        new PointOfInterest();
    }

    @Test
    public void testGetName() {
        Assert.assertThat(p1.getName(), is("credit suisse"));
    }

    @Test
    public void testGetType() {
        Assert.assertThat(p1.getType(), is(PointOfInterestType.ATM));
    }

    @Test
    public void testGetLocation() {
        Assert.assertThat(p1.getLocation(), is(new Location(24, 42)));
    }

    @Test
    public void testGetRadius() {
        Assert.assertThat(p1.getRadius(), is(0.003));
    }

    @Test
    public void testEqualsTwoEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest("credit suisse", PointOfInterestType.ATM, new Location(24, 42),
                0.003);
        Assert.assertEquals(p1, p2);
    }

    @Test
    public void testEqualsTwoNonEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest("credit suisse", PointOfInterestType.ATM, new Location(22, 42),
                0.03);
        Assert.assertNotEquals(p1, p2);
    }

    @Test
    public void testEqualsDifferentPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest("BCV", PointOfInterestType.ATM,
                new Location(24, 42), 0.003);
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest("BCV", PointOfInterestType.WC,
                new Location(24, 42), 0.003);
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest("credit suisse", PointOfInterestType.WC,
                new Location(24, 42), 0.003);
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest("credit suisse", PointOfInterestType.ATM,
                new Location(24, 42), 0.3);
        Assert.assertNotEquals(p1, p2);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        Assert.assertNotEquals(p1, new Object());
    }
}
