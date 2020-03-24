package ch.epfl.balelecbud.models;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class PointOfInterestTest {

    private PointOfInterest p1 = new PointOfInterest( new GeoPoint(24, 42), "credit suisse", "atm", "BXnkTQdLsOXoGJmMSeCS");

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
    public void testGetPoiToken() { Assert.assertThat(p1.getPoiToken(), is("BXnkTQdLsOXoGJmMSeCS"));}

    @Test
    public void testGetLocation() {
        Assert.assertThat(p1.getLocation(), is(new GeoPoint(24, 42)));
    }

    @Test
    public void testEqualsTwoEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new GeoPoint(24, 42), "credit suisse", "atm", "BXnkTQdLsOXoGJmMSeCS");
        Assert.assertEquals(true, p1.equals(p2));
    }

    @Test
    public void testEqualsTwoNonEqualPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new GeoPoint(22, 42), "credit suisse", "atm", "BXnkTQdLsOXoGJmMSeCS");
        Assert.assertEquals(false, p1.equals(p2));
    }

    @Test
    public void testEqualsDifferentPointOfInterest() {
        PointOfInterest p2 = new PointOfInterest( new GeoPoint(24, 42), "BCV", "atm", "BXnkTQdLsOXoGJmMSeCS");
        Assert.assertEquals(false, p1.equals(p2));

        p2 = new PointOfInterest( new GeoPoint(24, 42), "BCV", "bancomat", "BXnkTQdLsOXoGJmMSeCS");
        Assert.assertEquals(false, p1.equals(p2));

        p2 = new PointOfInterest( new GeoPoint(24, 42), "credit suisse", "atm", "0");
        Assert.assertEquals(false, p1.equals(p2));

        p2 = new PointOfInterest( new GeoPoint(24, 42), "credit suisse", "bancomat", "0");
        Assert.assertEquals(false, p1.equals(p2));
    }


    @Test
    public void testEqualsTwoDifferentObjects() {
        Assert.assertEquals(false, p1.equals(new Object()));
    }


}
