package ch.epfl.balelecbud.view.emergency;

import android.os.SystemClock;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.EmergencyNumber;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class EmergencyNumberActivityTest {


    final EmergencyNumber num1 = new EmergencyNumber("Help","115");
    private final MockDatabase mock = MockDatabase.getInstance();

    @Before
    public void setup() {
        mock.resetDatabase();
        mock.resetDocument(Database.EMERGENCY_NUMBER_PATH);
        mock.storeDocument(Database.EMERGENCY_NUMBER_PATH, num1);
        BalelecbudApplication.setAppDatabase(mock);
        FragmentScenario.launchInContainer(EmergencyNumbersFragment.class);
    }

    @Test
    public void testListViewIsDisplayed() {
        onView(ViewMatchers.withId(R.id.numbersListView)).check(matches(isDisplayed()));
    }

    @Test
    public void testElementDisplayed() {

        SystemClock.sleep(2000);

        onData(anything())
                .inAdapterView(withId(R.id.numbersListView))
                .atPosition(0)
                .check(matches(withText(num1.getName())));

    }
}