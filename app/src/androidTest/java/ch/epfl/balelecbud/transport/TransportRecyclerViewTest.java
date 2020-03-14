package ch.epfl.balelecbud.transport;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.TransportActivity;
import ch.epfl.balelecbud.matchers.RecyclerViewMatcher;
import ch.epfl.balelecbud.transport.objects.Transport;
import ch.epfl.balelecbud.transport.objects.TransportType;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

@RunWith(AndroidJUnit4.class)
public class TransportRecyclerViewTest {

    final Transport transport1 = new Transport(TransportType.BUS, 10, "Nyon", null, Timestamp.now());
    final Transport transport2 = new Transport(TransportType.METRO, 12, "EPFL", null, Timestamp.now());
    final Transport transport3 = new Transport(TransportType.BUS, 122, "La lune", null, Timestamp.now());

    TransportListener myListener;
    @Before
    public void setUp() {
        TransportDatabase mock = new TransportDatabase() {
            @Override
            public TransportListener getTransportListener(TransportAdapterFacade adapter, List<Transport> transports) {
                myListener = new TransportListener(adapter, transports, new WrappedListener() {
                    @Override
                    public void remove() {
                        //nothing
                    }

                    @Override
                    public void registerOuterListener(TransportListener outerListener) {
                        //nothing
                    }
                });
                return myListener;
            }
        };
        MyTransportRecyclerViewAdapter.setTransportDatabase(mock);
        ActivityScenario activityScenario = ActivityScenario.launch(TransportActivity.class);

    }
    private void compareViewAndItem(ViewInteraction viewInt, Transport transport){
        viewInt.check(matches(hasDescendant(withText(transport.getTypeString()))));
        viewInt.check(matches(hasDescendant(withText(transport.getLineString()))));
        viewInt.check(matches(hasDescendant(withText(transport.getDirection()))));
        viewInt.check(matches(hasDescendant(withText(transport.getTimeString()))));
    }

    private void addItem(final Transport t, final int index) throws Throwable {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                myListener.addTransport(t, index);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }
    }
    private void modifyItem(final Transport t, final int index) throws Throwable {

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                myListener.modifyTransport(t, index);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }


    }
    private void removeItem(final int index) throws Throwable {

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                myListener.removeTransport(index);
            }
        };

        runOnUiThread(myRunnable);
        synchronized (myRunnable){
            myRunnable.wait(1000);
        }


    }

    @Test
    public void displayIsUpdatedWhenItemsAdded() throws Throwable {

        onView(withId(R.id.fragment)).check(matches(hasChildCount(0)));

        addItem(transport1, 0);

        onView(withId(R.id.fragment)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)), transport1);
        onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)).perform(click());

        addItem(transport2, 1);
        onView(withId(R.id.fragment)).check(matches(hasChildCount(2)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)), transport1);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(1)), transport2);

        addItem(transport3, 2);
        onView(withId(R.id.fragment)).check(matches(hasChildCount(3)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)), transport1);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(1)), transport2);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(2)), transport3);
    }

    @Test
    public void displayIsUpdatedWhenItemsModified() throws Throwable {

        onView(withId(R.id.fragment)).check(matches(hasChildCount(0)));

        addItem(transport1, 0);

        onView(withId(R.id.fragment)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)), transport1);

        modifyItem(transport2, 0);
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)), transport2);
    }

    @Test
    public void displayIsUpdatedWhenItemsRemoved() throws Throwable {

        onView(withId(R.id.fragment)).check(matches(hasChildCount(0)));

        addItem(transport1, 0);

        onView(withId(R.id.fragment)).check(matches(hasChildCount(1)));
        compareViewAndItem(onView(new RecyclerViewMatcher(R.id.fragment).atPosition(0)), transport1);

        removeItem(0);

        onView(withId(R.id.fragment)).check(matches(hasChildCount(0)));
    }

    @Test
    public void canCreateOtherFragment() {
        TransportListFragment.newInstance();
    }

}
