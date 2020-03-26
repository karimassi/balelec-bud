package ch.epfl.balelecbud.schedule.models;

import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SlotTest {

    static private List<Timestamp> timestamps;
    static private Slot slot1;
    static private Slot slot2;
    static private Slot sameAsSlot1;

    @BeforeClass
    public static void setUpSlots() {
        timestamps = new LinkedList<>();
        for (int i = 0; i < 6; ++i) {
            Calendar c = Calendar.getInstance();
            c.set(2020, 11, 11, 10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3));
        sameAsSlot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
    }


    @Test
    public void testEmptyConstructor() {
        new Slot();
    }

    @Test
    public void testGettersReturnsCorrectly() {
        Assert.assertEquals("Mr Oizo", slot1.getArtistName());
        Assert.assertEquals("Grande scène", slot1.getSceneName());
        Assert.assertEquals("10:15 - 11:00", slot1.getTimeSlot());
    }

    @Test
    public void testEqualsTwoEqualSlots() {
        Assert.assertEquals(slot1, sameAsSlot1);
    }

    @Test
    public void testEqualsTwoNonEqualSlots() {
        Assert.assertNotEquals(slot1, slot2);
    }

    @Test
    public void testEqualsTwoNonEqualSceneName() {
        Slot slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        Slot slot2 = new Slot(0, "Mr Oizo", "Petite scène", timestamps.get(0), timestamps.get(1));
        Assert.assertNotEquals(slot1, slot2);
    }

    @Test
    public void testEqualsTwoNonEqualStartTime() {
        Slot slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(2));
        Slot slot2 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(1), timestamps.get(2));
        Assert.assertNotEquals(slot1, slot2);
    }

    @Test
    public void testEqualsTwoNonEqualEndTime() {
        Slot slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        Slot slot2 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(2));
        Assert.assertNotEquals(slot1, slot2);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        Slot slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        Assert.assertNotEquals(slot1, new Object());
    }

    @Test
    public void testEqualsTwoNonEqualId() {
        Slot slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        Slot slot2 = new Slot(1, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        Assert.assertNotEquals(slot1, slot2);
    }
}
