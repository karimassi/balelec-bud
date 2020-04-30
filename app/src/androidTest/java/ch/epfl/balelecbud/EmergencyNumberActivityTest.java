package ch.epfl.balelecbud;

import androidx.test.rule.ActivityTestRule;

import android.os.SystemClock;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import ch.epfl.balelecbud.emergency.models.EmergencyNumber;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;


@RunWith(AndroidJUnit4.class)
public class EmergencyNumberActivityTest extends BasicActivityTest {


    final EmergencyNumber num1 = new EmergencyNumber("Help","115");
    final EmergencyNumber num2 = new EmergencyNumber("More help","1234");
    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();


    @Rule
    public final ActivityTestRule<EmergencyNumbersActivity> mActivityRule = new ActivityTestRule<EmergencyNumbersActivity>(EmergencyNumbersActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppDatabaseWrapper(mock);
            mock.resetDocument(DatabaseWrapper.EMERGENCY_NUMBER_PATH);
            mock.storeDocument(DatabaseWrapper.EMERGENCY_NUMBER_PATH, num1);
        }
    };

    @Test
    public void testListViewIsNonNull() {
        EmergencyNumbersActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.numbersListView);
        assertThat(viewById, notNullValue());
    }

    @Test
    public void testListViewIsDisplayed() {
        onView(withId(R.id.numbersListView)).check(matches(isDisplayed()));
    }

    @Test
    public void testElementDisplayed() {

        SystemClock.sleep(2000);

        onData(anything())
                .inAdapterView(withId(R.id.numbersListView))
                .atPosition(0)
                .check(matches(withText(num1.getName())));

    }


    @Override
    protected void setIds() {
        setIds(R.id.emergency_numbers_activity_drawer_layout, R.id.emergency_numbers_activity_nav_view);

    }
}

