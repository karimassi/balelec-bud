package ch.epfl.balelecbud.model;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class EmergencyNumberTest {

    @Test
    public void testEmptyConstructor() {
        new EmergencyNumber();
    }

    @Test
    public void testGetInfoReturnsCorrectly() {
        EmergencyNumber first = new EmergencyNumber("a number", "1234");
        Assert.assertEquals("a number", first.getName());
        Assert.assertEquals("1234", first.getNumber());
    }

    @Test
    public void testEqualsTwoEqualEmergencyInfo() {
        EmergencyNumber first = new EmergencyNumber("a number", "1234");
        EmergencyNumber second = new EmergencyNumber("a number", "1234");
        Assert.assertEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyInfo() {
        EmergencyNumber first = new EmergencyNumber("a number", "1234");
        EmergencyNumber second = new EmergencyNumber("a number", "4321");
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoNonEqualEmergencyTitle() {
        EmergencyNumber first = new EmergencyNumber("a number", "info");
        EmergencyNumber second = new EmergencyNumber("a second number", "info");
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        EmergencyNumber first = new EmergencyNumber("a number", "1234");
        Assert.assertNotEquals(first, new Object());
    }

}
