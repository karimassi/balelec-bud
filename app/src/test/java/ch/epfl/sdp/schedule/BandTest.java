package ch.epfl.sdp.schedule;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.Assert.*;


public class BandTest {

    String name;
    Optional<String> description;
    Optional<Bitmap> picture;

    @Before
    public void setup(){
        name = "My band";
        description = Optional.empty();
        picture = Optional.empty();
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsWhenNameIsNull(){
        Band test = new Band(null, description, picture);
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsWhenDescriptionIsNull(){
        Band test = new Band(name, null, picture);
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsWhenPictureIsNull(){
        Band test = new Band(name, description, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFailsWhenNameIsEmpty(){
        Band test = new Band("", description, picture);
    }

    @Test
    public void getNameReturnsExpectedName(){
        Band test = new Band(name, description, picture);
        assertEquals(name, test.getName());
    }

    @Test
    public void getDescriptionReturnsExpectedDescription(){
        Band test = new Band(name, description, picture);
        assertEquals(description, test.getDescription());
    }

    @Test
    public void getPictureReturnsExpectedPicture(){
        Band test = new Band(name, description, picture);
        assertEquals(picture, test.getPicture());
    }


}
