package ch.epfl.balelecbud.schedule;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;


public class ConcertTest {

    private TimeSlot timeSlot;
    private Band band;

    @Before
    public void setup(){
        TimeSlot.Time startingTime = new TimeSlot.Time(10, 32);
        TimeSlot.Time endingTime = new TimeSlot.Time(11, 23);
        timeSlot = new TimeSlot(startingTime, endingTime);
        String name = "My band";
        Optional<String> description = Optional.empty();
        Optional<Bitmap> picture = Optional.empty();
        band = new Band(name, description, picture);
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailWhenBandNull(){
        Concert test = new Concert(timeSlot, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailWhenTimeSlotNull() {
        Concert test = new Concert(null, band);
    }

    @Test
    public void getBandReturnsExpectedValue(){
        Concert test = new Concert(timeSlot, band);
        assertEquals(band, test.getBand());
    }

    @Test
    public void getTimeSlotReturnsExpectedValue(){
        Concert test = new Concert(timeSlot, band);
        assertEquals(timeSlot, test.getTimeSlot());
    }
}
