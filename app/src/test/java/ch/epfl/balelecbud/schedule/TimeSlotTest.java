package ch.epfl.balelecbud.schedule;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeSlotTest {

    TimeSlot.Time startingTime;
    TimeSlot.Time endingTime;
    TimeSlot timeSlot;

    @Before
    public void setup(){
        startingTime = new TimeSlot.Time(10, 32);
        endingTime = new TimeSlot.Time(11, 23);
        timeSlot = new TimeSlot(startingTime, endingTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeConstructorFailsWhenMinuteOver59(){
        TimeSlot.Time test = new TimeSlot.Time(2, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeConstructorFailsWhenMinuteNegative(){
        TimeSlot.Time test = new TimeSlot.Time(2, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeConstructorFailsWhenHourOver23(){
        TimeSlot.Time test = new TimeSlot.Time(24, 32);
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeConstructorFailsWhenHourNegative(){
        TimeSlot.Time test = new TimeSlot.Time(-1, 32);
    }

    @Test
    public void getMinuteReturnsExpectedValue(){
        TimeSlot.Time test = new TimeSlot.Time(10, 32);
        assertEquals(32, test.getMinute());
    }

    @Test
    public void getHourReturnsExpectedValue(){
        TimeSlot.Time test = new TimeSlot.Time(10, 32);
        assertEquals(10, test.getHour());
    }

    @Test(expected = NullPointerException.class)
    public void timeSlotConstructorFailsWhenStartingNull(){
        TimeSlot test = new TimeSlot(null, endingTime);
    }

    @Test(expected = NullPointerException.class)
    public void timeSlotConstructorFailsWhenEndingNull(){
        TimeSlot test = new TimeSlot(startingTime, null);
    }

    @Test
    public void getStartingReturnsExpectedValue(){
        assertEquals(startingTime.getMinute(), timeSlot.getStartingTime().getMinute());
        assertEquals(startingTime.getHour(), timeSlot.getStartingTime().getHour());
    }

    @Test
    public void getEndingReturnsExpectedValue(){
        assertEquals(endingTime.getMinute(), timeSlot.getEndingTime().getMinute());
        assertEquals(endingTime.getHour(), timeSlot.getEndingTime().getHour());
    }


}
