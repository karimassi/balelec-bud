package ch.epfl.balelecbud.FestivalInformation;

import org.junit.Assert;
import org.junit.Test;

public class FestivalInformationTest {

    @Test
    public void testEmptyConstructor() {
        new FestivalInformation();
    }

    @Test
    public void testGetInfoReturnsCorrectly() {
        FestivalInformation first = new FestivalInformation("title", "info");
        Assert.assertEquals("title", first.getTitle());
        Assert.assertEquals("info", first.getInformation());
    }

    @Test
    public void testEqualsTwoEqualFestivalInfo() {
        FestivalInformation first = new FestivalInformation("title", "info");
        FestivalInformation second = new FestivalInformation("title", "info");
        Assert.assertEquals(true, first.equals(second));
    }
    @Test
    public void testEqualsTwoNonEqualFestivalInfo() {
        FestivalInformation first = new FestivalInformation("title", "info2");
        FestivalInformation second = new FestivalInformation("title", "info1");
        Assert.assertEquals(false, first.equals(second));
    }

    @Test
    public void testEqualsTwoNonEqualFestivalTitle() {
        FestivalInformation first = new FestivalInformation("title1", "info");
        FestivalInformation second = new FestivalInformation("title2", "info");
        Assert.assertEquals(false, first.equals(second));
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        FestivalInformation first = new FestivalInformation("title1", "info");
        Assert.assertEquals(false, first.equals(new Object()));
    }


}
