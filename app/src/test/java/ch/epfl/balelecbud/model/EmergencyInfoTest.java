package ch.epfl.balelecbud.model;


import org.junit.Assert;
import org.junit.Test;


import static org.hamcrest.core.Is.is;

public class EmergencyInfoTest {

    @Test
    public void testEmptyConstructor() {
        new EmergencyInfo();
    }

    @Test
    public void testGetInfoReturnsCorrectly() {
        EmergencyInfo first = new EmergencyInfo("title", "info");
        Assert.assertEquals("title", first.getName());
        Assert.assertEquals("info", first.getInstruction());
    }

    @Test
    public void testEqualsTwoEqualEmergencyInfo() {
        EmergencyInfo first = new EmergencyInfo("title", "info");
        EmergencyInfo second = new EmergencyInfo("title", "info");
        Assert.assertEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyInfo() {
        EmergencyInfo first = new EmergencyInfo("title", "info2");
        EmergencyInfo second = new EmergencyInfo("title", "info1");
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyTitle() {
        EmergencyInfo first = new EmergencyInfo("title1", "info");
        EmergencyInfo second = new EmergencyInfo("title2", "info");
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        EmergencyInfo first = new EmergencyInfo("title1", "info");
        Assert.assertNotEquals(first, new Object());
    }

}
