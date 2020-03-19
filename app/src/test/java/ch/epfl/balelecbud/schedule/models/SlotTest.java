package ch.epfl.balelecbud.schedule.models;

import org.junit.Assert;
import org.junit.Test;

public class SlotTest {

    @Test
    public void testEmptyConstructor() {
        new Slot();
    }

    @Test
    public void testGettersReturnsCorrectly() {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Assert.assertEquals("Mr Oizo", slot1.getArtistName());
        Assert.assertEquals("Grande scène", slot1.getSceneName());
        Assert.assertEquals("19h - 20h", slot1.getTimeSlot());
    }

    @Test
    public void testEqualsTwoEqualSlots() {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Assert.assertEquals(true, slot1.equals(slot2));
    }

    @Test
    public void testEqualsTwoNonEqualSlots() {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Mme Oizo", "Grande scène", "19h - 20h");
        Assert.assertEquals(false, slot1.equals(slot2));
    }

    @Test
    public void testEqualsTwoNonEqualSceneName() {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Mr Oizo", "Petite scène", "19h - 20h");
        Assert.assertEquals(false, slot1.equals(slot2));
    }

    @Test
    public void testEqualsTwoNonEqualTimeSlot() {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Slot slot2 = new Slot("Mr Oizo", "Grande scène", "20h - 21h");
        Assert.assertEquals(false, slot1.equals(slot2));
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        Slot slot1 = new Slot("Mr Oizo", "Grande scène", "19h - 20h");
        Assert.assertEquals(false, slot1.equals(new Object()));
    }


}
