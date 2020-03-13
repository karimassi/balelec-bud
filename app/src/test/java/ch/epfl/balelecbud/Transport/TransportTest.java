package ch.epfl.balelecbud.Transport;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.Transport.Object.Transport;
import ch.epfl.balelecbud.Transport.Object.TransportType;

import static org.hamcrest.core.Is.is;

public class TransportTest {
    private Transport t1 = new Transport(TransportType.BUS, 42, "Home",
            new GeoPoint(24, 42), new Timestamp(0, 0));
    @Test
    public void testGetType() {
        Assert.assertThat(t1.getType(), is(TransportType.BUS));
    }

    @Test
    public void testGetTypeString() {
        Assert.assertThat(t1.getTypeString(), is(TransportType.BUS.toString()));
    }

    @Test
    public void testGetLine() {
        Assert.assertThat(t1.getLine(), is(42));
    }

    @Test
    public void testGetLineString() {
        Assert.assertThat(t1.getLineString(), is("42"));
    }

    @Test
    public void testGetDirection() {
        Assert.assertThat(t1.getDirection(), is("Home"));
    }

    @Test
    public void testGetPoint() {
        Assert.assertThat(t1.getPoint(), is(new GeoPoint(24, 42)));
    }

    @Test
    public void testGetTime() {
        Assert.assertThat(t1.getTime(), is(new Timestamp(0, 0)));
    }
}
