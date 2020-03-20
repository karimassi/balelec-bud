package ch.epfl.balelecbud.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FriendRequestTest {

    private FriendRequest request;


    @Before
    public void setup() {
        request = new FriendRequest("12", "21", 0);
    }


    @Test
    public void testEmptyConstructor() {new FriendRequest();}

    @Test
    public void testGetters() {
        Assert.assertEquals("12", request.getSenderId());
        Assert.assertEquals("21", request.getRecipientId());
        Assert.assertEquals(0, request.getStatus());
    }

    @Test
    public void testEquals() {
        FriendRequest otherRequest = new FriendRequest("31", "34", 1);
        Assert.assertEquals(false, request.equals(otherRequest));

        otherRequest = new FriendRequest("12", "34", 1);
        Assert.assertEquals(false, request.equals(otherRequest));

        otherRequest = new FriendRequest( "12", "34", 0);
        Assert.assertEquals(false, request.equals(otherRequest));

        otherRequest = new FriendRequest( "12", "21", 1);
        Assert.assertEquals(false, request.equals(otherRequest));

        otherRequest = new FriendRequest( "3", "21", 0);
        Assert.assertEquals(false, request.equals(otherRequest));
        Assert.assertNotEquals(otherRequest.hashCode(), request.hashCode());

        otherRequest = new FriendRequest( "12", "21", 0);
        Assert.assertEquals(true, request.equals(otherRequest));
        Assert.assertEquals(otherRequest.hashCode(), request.hashCode());

        Assert.assertEquals(true, request.equals(request));
        Assert.assertEquals(false, request.equals(null));
        Assert.assertEquals(false, request.equals(new Object()));

    }

    @Test (expected = IllegalArgumentException.class)
    public void testSameUsers() {
        FriendRequest otherRequest = new FriendRequest("31", "31", 0);
    }





}
