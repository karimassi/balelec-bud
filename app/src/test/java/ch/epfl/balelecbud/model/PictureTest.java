package ch.epfl.balelecbud.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.balelecbud.gallery.Picture;

public class PictureTest {
    static private Picture picture1;
    static private Picture picture2;
    static private Picture picture1bis;

    @BeforeClass
    public static void setUpPictures() {
        picture1 = new Picture("picture1ImageName");
        picture2 = new Picture("picture2ImageName");
        picture1bis = new Picture("picture1ImageName");
    }

    @Test
    public void testGettersReturnsCorrectly() {
        Assert.assertEquals("picture1ImageName", picture1.getImageFileName());
        Assert.assertEquals("picture2ImageName", picture2.getImageFileName());
        Assert.assertEquals("picture1ImageName", picture1bis.getImageFileName());
    }

    @Test
    public void testEqualsTwoEqualPictures() {
        Assert.assertEquals(picture1bis, picture1);
        Assert.assertEquals(picture1, picture1bis);
    }

    @Test
    public void testEqualsTwoNonEqualPictures() {
        Assert.assertNotEquals(picture1, picture2);
    }

    @Test
    public void testEqualsTwoDifferentObjects() {
        Assert.assertNotEquals(picture1, new Object());
        Assert.assertNotEquals(new Object(), picture1);
    }
}
