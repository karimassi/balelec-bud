package ch.epfl.balelecbud;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.notifications.concertFlow.AbstractConcertFlow;
import ch.epfl.balelecbud.notifications.concertFlow.FlowUtil;
import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.ScheduleActivityTest.nthChildOf;
import static ch.epfl.balelecbud.ScheduleActivityTest.switchChecked;
import static org.hamcrest.core.Is.is;

public class ScheduleActivityWithSubscribedConcertTest {
    private MockDatabaseWrapper mock;

    static private Slot slot1;
    static private Slot slot2;

    @BeforeClass
    public static void setUpSlots(){
        List<Timestamp> timestamps = new LinkedList<>();
        for(int i = 0; i < 6; ++i){
            Calendar c = Calendar.getInstance();
            c.set(2020,11,11,10 + i, i % 2 == 0 ? 15 : 0);
            Date date = c.getTime();
            timestamps.add(i, new Timestamp(date));
        }
        slot1 = new Slot(0, "Mr Oizo", "Grande scène", timestamps.get(0), timestamps.get(1));
        slot2 = new Slot(1, "Walking Furret", "Les Azimutes", timestamps.get(2), timestamps.get(3)) ;
    }

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule = new ActivityTestRule<ScheduleActivity>(ScheduleActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            mock = new MockDatabaseWrapper();
            ScheduleAdapter.setDatabaseImplementation(mock);
        }
    };

    @Test
    public void testUnSubscribeToAConcert() throws Throwable {
        final List<Object> sync = new LinkedList<>();
        mActivityRule.getActivity().setServiceInterface(new ScheduleAdapter.ServiceInterface() {
            @Override
            public void callService(@NotNull Intent intent) {
                if (intent.getAction() == null)
                    Assert.fail();

                String action = intent.getAction();
                switch (action) {
                    case FlowUtil.ACK_CONCERT:
                        Assert.fail();
                        break;
                    case FlowUtil.CANCEL_CONCERT:
                        synchronized (sync) {
                            sync.add(new Object());
                            sync.notify();
                        }
                    case FlowUtil.SUBSCRIBE_CONCERT:
                        Assert.assertThat(FlowUtil.unpackSlotInIntent(intent), is(slot1));
                        break;
                    case FlowUtil.GET_ALL_CONCERT:
                        AbstractConcertFlow.FlowCallback callback = FlowUtil.unpackCallback(intent);
                        callback.onResult(Collections.singletonList(slot1));
                        break;
                }
            }
        });
        mock.addItem(slot1);
        mock.addItem(slot2);
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).perform(click());
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertEquals(sync.size(), 1);
    }

    @Test
    public void testSubscribedConcertIsChecked() throws Throwable {
        mock.addItem(slot1);
        mock.addItem(slot2);

        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 3)).check(switchChecked(true));
        onView(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 3)).check(switchChecked(false));
    }
}
