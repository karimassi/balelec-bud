package ch.epfl.balelecbud.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HelpPageTest {

    private String myTitle1 = "my title";
    private String myTitle2 = "my other title";
    private String myDesc1 = "my description";
    private String myDesc2 = "my other description";

    private HelpPage helpPage1 = new HelpPage(myTitle1, myDesc1);
    private HelpPage helpPage2 = new HelpPage(myTitle2, myDesc2);
    private HelpPage sameAsHelpPage1 = new HelpPage(new String(myTitle1), new String(myDesc1));

    @Test
    public void getTitleReturnsCorrectTitle(){
        assertEquals(myTitle1, helpPage1.getTitle());
    }

    @Test
    public void getDescriptionReturnsCorrectDescription(){
        assertEquals(myDesc1, helpPage1.getDescription());
    }

    @Test
    public void equalsFailsWhenDifferentFields(){
        assertNotEquals(helpPage1, helpPage2);
    }

    @Test
    public void equalsSucceedsWhenSameFields(){
        assertEquals(helpPage1, sameAsHelpPage1);
    }

    @Test
    public void hashCodeReturnsSameWhenEquals(){
        assertEquals(helpPage1, sameAsHelpPage1);
        assertEquals(helpPage1.hashCode(), sameAsHelpPage1.hashCode());
    }

}
