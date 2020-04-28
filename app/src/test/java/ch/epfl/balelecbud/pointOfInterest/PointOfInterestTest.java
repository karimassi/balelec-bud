package ch.epfl.balelecbud.pointOfInterest;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.models.Location;

import static org.hamcrest.core.Is.is;

public class PointOfInterestTest {

    private final PointOfInterest p1 =
            new PointOfInterest(new Location(24, 42),
                    "credit suisse", PointOfInterestType.ATM);

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
    public void testEqualsTwoEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest(new Location(24, 42),
                "credit suisse", PointOfInterestType.ATM);
        Assert.assertEquals(p1, p2);
    }

    @Test
    public void testEqualsTwoNonEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new Location(22, 42),
                "credit suisse", PointOfInterestType.ATM);
        Assert.assertNotEquals(p1, p2);
    }

    @Test
    public void testEqualsDifferentPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new Location(24, 42),
                "BCV", PointOfInterestType.ATM);
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest( new Location(24, 42), "BCV",
                PointOfInterestType.WC);
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest( new Location(24, 42),
                "credit suisse", PointOfInterestType.WC);
        Assert.assertNotEquals(p1, p2);
    }


    @Test
    public void testEqualsTwoDifferentObjects() {
        Assert.assertNotEquals(p1, new Object());
    }


}
