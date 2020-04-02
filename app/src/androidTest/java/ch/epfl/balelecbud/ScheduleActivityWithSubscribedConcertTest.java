package ch.epfl.balelecbud;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.schedule.ScheduleAdapter;
import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.intents.IntentLauncher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.ScheduleActivityTest.nthChildOf;
import static ch.epfl.balelecbud.ScheduleActivityTest.switchChecked;

@RunWith(AndroidJUnit4.class)
public class ScheduleActivityWithSubscribedConcertTest {
    private MockDatabaseWrapper mock;

    static private Slot slot1;
    static private Slot slot2;

    @BeforeClass
    public static void setUpSlots(){
        List<Timestamp> timestamps = new LinkedList<>();
        for(int i = 0; i < 4; ++i){
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
            mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
            ScheduleAdapter.setDatabaseImplementation(mock);
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ScheduleActivity.class);
            FlowUtil.packCallback(new Slot[] { slot1 }, intent);
            return intent;
        }
    };

    @Before
    public void setUpMockIntentLauncher() {
        this.mActivityRule.getActivity().setIntentLauncher(new IntentLauncher() {
            @Override
            public void launchIntent(@NonNull Intent intent) {

            }
        });
    }

    @Test
    public void testUnSubscribeToAConcert() throws Throwable {
        final List<Object> sync = new LinkedList<>();
        mActivityRule.getActivity().setIntentLauncher(new IntentLauncher() {
            @Override
            public void launchIntent(@NonNull Intent intent) {
                if (intent.getAction() == null)
                    Assert.fail();

                String action = intent.getAction();
                switch (action) {
                    case FlowUtil.ACK_CONCERT:
                    case FlowUtil.GET_ALL_CONCERT:
                        Assert.fail();
                        break;
                    case FlowUtil.CANCEL_CONCERT:
                        synchronized (sync) {
                            sync.add(new Object());
                            sync.notify();
                        }
                    case FlowUtil.SUBSCRIBE_CONCERT:
                        break;
                }
            }
        });
        mock.addItem(slot1);
        mock.addItem(slot2);
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 3)).perform(click());
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertEquals(sync.size(), 1);
    }

    @Test
    public void testSubscribedConcertIsChecked() throws Throwable {
        mock.addItem(slot1);
        mock.addItem(slot2);

        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 0), 1), 3)).check(switchChecked(true));
        onView(nthChildOf(nthChildOf(nthChildOf(withId(R.id.scheduleRecyclerView), 1), 1), 3)).check(switchChecked(false));
    }
}
