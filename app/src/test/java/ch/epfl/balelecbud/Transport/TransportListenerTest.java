package ch.epfl.balelecbud.Transport;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.balelecbud.Transport.Object.Transport;
import ch.epfl.balelecbud.Transport.Object.TransportType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(JUnit4.class)
public class TransportListenerTest {
    private WrappedListener dummyWrappedListener = new WrappedListener() {
        @Override
        public void remove() {
            Assert.fail();
        }

        @Override
        public void registerOuterListener(TransportListener outerListener) {
        }
    };

    private Transport t1 = new Transport(TransportType.BUS, 42, "Home",
            new GeoPoint(24, 42), new Timestamp(Date.from(Instant.EPOCH)));
    private Transport t2 = new Transport(TransportType.METRO, 2, "Flon",
            new GeoPoint(4, 45), new Timestamp(Date.from(Instant.ofEpochMilli(12345))));
    private Transport t3 = new Transport(TransportType.BUS, 0, "Lac",
            new GeoPoint(14, 5), new Timestamp(Date.from(Instant.ofEpochMilli(65432))));

    @Test
    public void canAddTransport() {
        List<Transport> l = new ArrayList<>();
        final int index = 0;

        TransportListener tl = new TransportListener(new TransportAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                assertThat(position, is(index));
            }

            @Override
            public void notifyItemChanged(int position) {
                Assert.fail();
            }

            @Override
            public void notifyItemRemoved(int position) {
                Assert.fail();
            }
        }, l, dummyWrappedListener);


        tl.addTransport(t1, index);
        assertThat(l, is(Collections.singletonList(t1)));
    }

    @Test
    public void canRemoveTransport() {
        List<Transport> l = new ArrayList<>(Arrays.asList(t1, t2, t3));
        final int index = 1;

        TransportListener tl = new TransportListener(new TransportAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                Assert.fail();
            }

            @Override
            public void notifyItemChanged(int position) {
                Assert.fail();
            }

            @Override
            public void notifyItemRemoved(int position) {
                assertThat(position, is(index));
            }
        }, l, dummyWrappedListener);

        tl.removeTransport(index);
        assertThat(l, is(Arrays.asList(t1, t3)));
    }

    @Test
    public void canModifyTransport() {
        List<Transport> l = new ArrayList<>(Arrays.asList(t1, t2, t2));
        final int index = 2;

        TransportListener tl = new TransportListener(new TransportAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                Assert.fail();
            }

            @Override
            public void notifyItemChanged(int position) {
                assertThat(position, is(index));
            }

            @Override
            public void notifyItemRemoved(int position) {
                Assert.fail();
            }
        }, l, dummyWrappedListener);

        tl.modifyTransport(t3, index);
        assertThat(l, is(Arrays.asList(t1, t2, t3)));
    }
}