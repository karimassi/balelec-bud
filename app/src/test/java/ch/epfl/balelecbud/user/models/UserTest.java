package ch.epfl.balelecbud.user.models;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class UserTest {
    private User u1 = new User("test@email.com", new GeoPoint(24, 42), "name", "surname", "a token");

    @Test
    public void testEmptyConstructor() {
        new User();
    }

    @Test
    public void testGetEmail() {
        Assert.assertThat(u1.getEmail(), is("test@email.com"));
    }

    @Test
    public void testGetName() {
        Assert.assertThat(u1.getName(), is("name"));
    }

    @Test
    public void testGetSurname() {
        Assert.assertThat(u1.getSurname(), is("surname"));
    }

    @Test
    public void testGetUserToken() {
        Assert.assertThat(u1.getUserToken(), is("a token"));
    }

    @Test
    public void testGetLocation() {
        Assert.assertThat(u1.getLocation(), is(new GeoPoint(24, 42)));
    }


}
