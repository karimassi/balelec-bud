package ch.epfl.balelecbud.model;

import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;


public class EmergencyTest {

    private Emergency e1 = new Emergency(EmergencyCategory.THEFT, "Help please","a user id",new Timestamp(0, 0));

    @Test
    public void testGetCategory(){ Assert.assertThat(e1.getCategory(), is(EmergencyCategory.THEFT));}

    @Test
    public void testGetCategoryString(){ Assert.assertThat(e1.getCategory().toString(), is(EmergencyCategory.THEFT.toString()));}

    @Test
    public void testGetMessage(){ Assert.assertThat(e1.getMessage(), is("Help please"));}

    @Test
    public void testGetUserID(){ Assert.assertThat(e1.getUserID(), is("a user id"));}

    @Test
    public void testGetTimestamp(){ Assert.assertThat(e1.getTimestamp(), is(new Timestamp(0, 0)));}

    @Test
    public void testHashCode() {
        Emergency e2 = new Emergency(EmergencyCategory.THEFT,"Help please","a user id",new Timestamp(0, 0));
        Assert.assertEquals(e2.hashCode(), e1.hashCode());

        e2 = new Emergency(EmergencyCategory.FAINT,"Help please","a user id",new Timestamp(0, 0));
        Assert.assertNotEquals(e1.hashCode(), e2.hashCode());

    }

    @Test
    public void testEquals() {
        Emergency e2 = new Emergency(EmergencyCategory.FAINT, "Help please","a user id",new Timestamp(0, 0));
        Assert.assertFalse(e1.equals(e2));

        e2 = new Emergency(EmergencyCategory.THEFT, "Help please","a user id",new Timestamp(0, 0));
        Assert.assertTrue(e1.equals(e2));
        Assert.assertFalse(e1.equals(new Object()));
    }
}
