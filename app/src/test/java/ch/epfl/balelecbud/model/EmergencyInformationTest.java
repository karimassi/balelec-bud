package ch.epfl.balelecbud.model;


import org.junit.Assert;
import org.junit.Test;

public class EmergencyInformationTest {

    @Test
    public void testEmptyConstructor() {
        new EmergencyInformation();
    }

    @Test
    public void testGetInfoReturnsCorrectly() {
        EmergencyInformation first = new EmergencyInformation("title", "info", false);
        Assert.assertEquals("title", first.getName());
        Assert.assertEquals("info", first.getInstruction());
    }

    @Test
    public void testEqualsTwoEqualEmergencyInfo() {
        EmergencyInformation first = new EmergencyInformation("title", "info", false);
        EmergencyInformation second = new EmergencyInformation("title", "info", false);
        Assert.assertEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyInfo() {
        EmergencyInformation first = new EmergencyInformation("title", "info2", false);
        EmergencyInformation second = new EmergencyInformation("title", "info1", false);
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyTitle() {
        EmergencyInformation first = new EmergencyInformation("title1", "info", false);
        EmergencyInformation second = new EmergencyInformation("title2", "info", false);
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyIsNumber() {
        EmergencyInformation first = new EmergencyInformation("title1", "info", false);
        EmergencyInformation second = new EmergencyInformation("title1", "info", true);
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        EmergencyInformation first = new EmergencyInformation("title1", "info", false);
        Assert.assertNotEquals(first, new Object());
    }

}
