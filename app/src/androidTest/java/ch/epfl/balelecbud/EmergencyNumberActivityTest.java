package ch.epfl.balelecbud;

import android.os.SystemClock;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.emergency.models.EmergencyNumber;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.IsNull.notNullValue;


@RunWith(AndroidJUnit4.class)
public class EmergencyNumberActivityTest extends RootActivityTest {


    final EmergencyNumber num1 = new EmergencyNumber("Help","115");
    final EmergencyNumber num2 = new EmergencyNumber("More help","1234");
    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();

    @Override
    protected void setUpBeforeActivityLaunched() {
        super.setUpBeforeActivityLaunched();
        BalelecbudApplication.setAppDatabaseWrapper(mock);
        mock.resetDocument(DatabaseWrapper.EMERGENCY_NUMBER_PATH);
        mock.storeDocument(DatabaseWrapper.EMERGENCY_NUMBER_PATH, num1);
    }

    @Test
    public void testListViewIsNonNull() {
        RootActivity mActivity = mActivityRule.getActivity();
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
    protected int getItemId() {
        return R.id.activity_main_drawer_emergency_numbers;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.emergency_number_layout;
    }
}

