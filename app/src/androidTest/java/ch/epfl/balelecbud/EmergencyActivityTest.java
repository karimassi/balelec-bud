package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class EmergencyActivityTest extends BasicActivityTest {


    private final User user = MockDatabaseWrapper.karim;
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final Authenticator mockAuthenticator = MockAuthenticator.getInstance();



    @Rule
    public final ActivityTestRule<EmergencyActivity> mActivityRule =
            new ActivityTestRule<EmergencyActivity>(EmergencyActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    BalelecbudApplication.setAppDatabaseWrapper(MockDatabaseWrapper.getInstance());
                    BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
                }
            };


    @Before
    public void setup() {
        mockDB.resetDocument(DatabaseWrapper.EMERGENCIES_PATH);
        mockAuthenticator.signOut();
        mockAuthenticator.setCurrentUser(user);
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
    public void emergencyIsCorrectlySent() throws ExecutionException, InterruptedException {

        submitEmergency("Theft", "I lost something");
        Emergency res = mockDB.getDocumentWithFieldCondition(DatabaseWrapper.EMERGENCIES_PATH, "category", "Theft", Emergency.class).get();
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

    @Override
    protected void setIds() {
    }
}
