package ch.epfl.balelecbud.festivalInformation;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;

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
        Assert.assertEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualFestivalInfo() {
        FestivalInformation first = new FestivalInformation("title", "info2");
        FestivalInformation second = new FestivalInformation("title", "info1");
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualFestivalTitle() {
        FestivalInformation first = new FestivalInformation("title1", "info");
        FestivalInformation second = new FestivalInformation("title2", "info");
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        FestivalInformation first = new FestivalInformation("title1", "info");
        Assert.assertNotEquals(first, new Object());
    }


}
