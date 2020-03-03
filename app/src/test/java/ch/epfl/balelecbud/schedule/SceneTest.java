package ch.epfl.balelecbud.schedule;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class SceneTest {

    Concert firstConcert;
    Concert secondConcert;
    Scene scene;

    private Concert createConcert(TimeSlot timeSlot, String bandName){
        Optional<String> description = Optional.empty();
        Optional<Bitmap> picture = Optional.empty();
        Band band = new Band(bandName, description, picture);
        return new Concert(timeSlot, band);
    }
    @Before
    public void setup(){
        firstConcert = createConcert(new TimeSlot(new TimeSlot.Time(10, 12), new TimeSlot.Time(11, 32)), "my first band");
        secondConcert = createConcert(new TimeSlot(new TimeSlot.Time(12, 12), new TimeSlot.Time(13, 32)), "my second band");
        scene = new Scene(new LinkedList<Concert>(Arrays.asList(firstConcert, secondConcert)));
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsWhenListIsNull(){
        Scene test = new Scene(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFailsWhenListIsEmpty(){
        Scene test = new Scene(new LinkedList<Concert>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listReturnCannotBeModified(){
        scene.getConcerts().remove(0);
    }

    @Test
    public void listReturnedIsCorrectView(){
        List<Concert> concerts = scene.getConcerts();
        assertEquals(firstConcert, concerts.get(0));
        assertEquals(secondConcert, concerts.get(1));
    }




}
