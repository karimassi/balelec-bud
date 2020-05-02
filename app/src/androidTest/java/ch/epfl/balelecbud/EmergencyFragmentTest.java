package ch.epfl.balelecbud;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.testng.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EmergencyFragmentTest extends RootActivityTest {
    private final User user = MockDatabase.karim;
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final Authenticator mockAuthenticator = MockAuthenticator.getInstance();

    @Override
    protected int getItemId() {
        return R.id.activity_main_drawer_emergency;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.fragment_emergency_linear_layout;
    }

    @Before
    public void setup() {
        mockDB.resetDocument(Database.EMERGENCIES_PATH);
        BalelecbudApplication.setAppDatabase(mockDB);
        FragmentScenario.launchInContainer(EmergencyFragment.class);
    }

    @Test
    public void testSubmitEmergencyButtonIsDisplayedWhenButtonClicked() {
        onView(withId(R.id.buttonAskForHelp)).perform(click());
        onView(withId(R.id.buttonEmergencySubmit)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonIsDisplayed() {
        onView(withId(R.id.buttonAskForHelp)).check(matches(isDisplayed()));
    }

    @Test
    public void emergencyIsCorrectlySent() throws Throwable {
        submitEmergency("Theft", "I lost something");
        Emergency res = mockDB.getDocumentWithFieldCondition(Database.EMERGENCIES_PATH, "category", "Theft", Emergency.class).get();
        assertThat(res, notNullValue());
        assertEquals(res.getMessage(), "I lost something");
    }


    private void submitEmergency(String category, String message) {
        onView(withId(R.id.buttonAskForHelp)).perform(click());
        onView(withId(R.id.spinnerEmergencyCategories)).perform(click());
        onView(withText(category)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.textEmergencyMessage)).perform(typeText(message)).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonEmergencySubmit)).perform(click());
    }
}