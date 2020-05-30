package ch.epfl.balelecbud.model;

import org.junit.Assert;
import org.junit.Test;

public class MarkerTypeTest {

    @Test
    public void getMarkerTypeReturnsCorrectly() {
        Assert.assertEquals(MarkerType.ATM, MarkerType.getMarkerType(PointOfInterestType.ATM));
        Assert.assertEquals(MarkerType.BAR, MarkerType.getMarkerType(PointOfInterestType.BAR));
        Assert.assertEquals(MarkerType.FIRST_AID, MarkerType.getMarkerType(PointOfInterestType.FIRST_AID));
        Assert.assertEquals(MarkerType.FOOD, MarkerType.getMarkerType(PointOfInterestType.FOOD));
        Assert.assertEquals(MarkerType.STAGE, MarkerType.getMarkerType(PointOfInterestType.STAGE));
        Assert.assertEquals(MarkerType.WC, MarkerType.getMarkerType(PointOfInterestType.WC));
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals(MarkerType.ATM.toString(), "ATM");
        Assert.assertEquals(MarkerType.BAR.toString(), "Bar");
        Assert.assertEquals(MarkerType.FIRST_AID.toString(), "First aid");
        Assert.assertEquals(MarkerType.FOOD.toString(), "Food");
        Assert.assertEquals(MarkerType.STAGE.toString(), "Stage");
        Assert.assertEquals(MarkerType.WC.toString(), "WC");

    }
}
