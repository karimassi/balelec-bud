package ch.epfl.balelecbud.pointOfInterest;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class PointOfInterestTest {

    private final PointOfInterest p1 =
            new PointOfInterest(new GeoPoint(24, 42),
                    "credit suisse", "atm");

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
        Assert.assertThat(p1.getType(), is("atm"));
    }

    @Test
    public void testGetLocation() {
        Assert.assertThat(p1.getLocation(), is(new GeoPoint(24, 42)));
    }

    @Test
    public void testEqualsTwoEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest(new GeoPoint(24, 42),
                "credit suisse", "atm");
        Assert.assertEquals(p1, p2);
    }

    @Test
    public void testEqualsTwoNonEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new GeoPoint(22, 42),
                "credit suisse", "atm");
        Assert.assertNotEquals(p1, p2);
    }

    @Test
    public void testEqualsDifferentPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new GeoPoint(24, 42),
                "BCV", "atm");
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest( new GeoPoint(24, 42), "BCV",
                "bancomat");
        Assert.assertNotEquals(p1, p2);

        p2 = new PointOfInterest( new GeoPoint(24, 42),
                "credit suisse", "bancomat");
        Assert.assertNotEquals(p1, p2);
    }


    @Test
    public void testEqualsTwoDifferentObjects() {
        Assert.assertNotEquals(p1, new Object());
    }


}
