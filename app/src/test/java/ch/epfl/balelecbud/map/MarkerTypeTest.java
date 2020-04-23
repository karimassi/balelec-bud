package ch.epfl.balelecbud.map;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.pointOfInterest.PointOfInterestType;

public class MarkerTypeTest {

    @Test
    public void getMarkerTypeReturnsCorrectly() {
        Assert.assertEquals(MarkerType.ATM, MarkerType.getMarkerType(PointOfInterestType.ATM));
        Assert.assertEquals(MarkerType.BAR, MarkerType.getMarkerType(PointOfInterestType.BAR));
        Assert.assertEquals(MarkerType.FIRST_AID, MarkerType.getMarkerType(PointOfInterestType.FIRST_AID));
        Assert.assertEquals(MarkerType.FOOD, MarkerType.getMarkerType(PointOfInterestType.FOOD));
        Assert.assertEquals(MarkerType.STAGE, MarkerType.getMarkerType(PointOfInterestType.STAGE));
        Assert.assertEquals(MarkerType.NO_TYPE, MarkerType.getMarkerType(PointOfInterestType.WC));
    }
}
