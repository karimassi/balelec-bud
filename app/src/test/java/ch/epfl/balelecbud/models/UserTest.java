package ch.epfl.balelecbud.models;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class UserTest {
    private final User u1 = new User("test@email.com", "displayName", "a token");

    @Test
    public void testEmptyConstructor() {
        new User();
    }

    @Test
    public void testGetEmail() {
        Assert.assertThat(u1.getEmail(), is("test@email.com"));
    }

    @Test
    public void testGetDisplayName() {
        Assert.assertThat(u1.getDisplayName(), is("displayName"));
    }

    @Test
    public void testGetUserToken() {
        Assert.assertThat(u1.getUid(), is("a token"));
    }

    @Test
    public void testEquals() {
        User u2 = new User("test2@email.com", "displayName", "a token");
        Assert.assertNotEquals(u1, u2);

        u2 = new User("test2@email.com", "name", "another token");
        Assert.assertNotEquals(u1, u2);

        u2 = new User("test@email.com", "name", "a token");
        Assert.assertNotEquals(u1, u2);

        u2 = new User("test@email.com", "displayName", "another token");
        Assert.assertNotEquals(u1, u2);

        u2 = new User("test@email.com", "displayName", "a token");
        Assert.assertEquals(u1, u2);

        Assert.assertNotEquals(u1, new Object());
    }

    @Test
    public void testHashCode() {
        User u2 = new User("test@email.com", "displayName", "a token");
        Assert.assertEquals(u2.hashCode(), u1.hashCode());

        u2 = new User("test@email.com", "name", "a token");
        Assert.assertNotEquals(u2.hashCode(), u1.hashCode());
    }
}
