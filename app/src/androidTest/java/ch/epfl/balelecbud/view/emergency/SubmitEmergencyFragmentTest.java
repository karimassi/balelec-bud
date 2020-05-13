package ch.epfl.balelecbud.view.emergency;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Emergency;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.utility.database.MockDatabase.karim;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
public class SubmitEmergencyFragmentTest {

    private FragmentScenario<SubmitEmergencyFragment> scenario;
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    @Before
    public void setup() {
        mockDB.resetDatabase();
        mockDB.resetDocument(MockDatabase.EMERGENCIES_PATH);
        mockAuth.setCurrentUser(karim);
        BalelecbudApplication.setAppDatabase(mockDB);
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        scenario = FragmentScenario.launch(SubmitEmergencyFragment.class, null, R.style.Theme_AppCompat, null);
    }

    @Test
    public void emergencyIsCorrectlySent() throws Throwable {
        submitEmergency("Theft", "I lost something");
        MyQuery query = new MyQuery(Database.EMERGENCIES_PATH, new ArrayList<>());
        TestAsyncUtils sync = new TestAsyncUtils();
        mockDB.query(query, Emergency.class).thenApply(emergencies -> emergencies.get(0)).whenComplete((emergency, throwable) -> {
            if (throwable == null) {
                sync.assertNotNull(emergency);
                sync.assertEquals(emergency.getMessage(), "I lost something");

            } else {
                sync.fail(throwable);
            }
            sync.call();
        });
        sync.waitCall(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void submitEmergencyEmptyMessage() {
        submitEmergency("Theft", "");
        onView(withText(R.string.emergency_ask_for_help)).check(matches(isDisplayed()));
    }

    @Test
    public void cancelDismissesFragment() {
        scenario.onFragment(fragment -> {
            assertThat(fragment.getDialog(), is(notNullValue()));
            assertThat(fragment.requireDialog().isShowing(), is(true));
            fragment.dismiss();
            fragment.getParentFragmentManager().executePendingTransactions();
            assertThat(fragment.getDialog(), is(nullValue()));
        });

    }


    private void submitEmergency(String category, String message) {
        onView(withId(R.id.spinner_emergency_categories)).perform(click());
        onView(withText(category)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edit_text_emergency_message)).perform(typeText(message)).perform(closeSoftKeyboard());
        onView(withText(R.string.submit_emergency)).perform(click());
    }
}