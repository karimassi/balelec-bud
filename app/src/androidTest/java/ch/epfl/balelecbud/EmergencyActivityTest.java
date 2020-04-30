package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;
import ch.epfl.balelecbud.util.database.MyQuery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EmergencyActivityTest extends BasicActivityTest {


    private final User user = MockDatabase.karim;
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final Authenticator mockAuthenticator = MockAuthenticator.getInstance();



    @Rule
    public final ActivityTestRule<EmergencyActivity> mActivityRule =
            new ActivityTestRule<EmergencyActivity>(EmergencyActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
                    BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
                }
            };


    @Before
    public void setup() {
        mockDB.resetDocument(Database.EMERGENCIES_PATH);
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
    public void emergencyIsCorrectlySent() throws Throwable {
        submitEmergency("Theft", "I lost something");
        MyQuery query = new MyQuery(Database.EMERGENCIES_PATH, new ArrayList<>());
        TestAsyncUtils sync = new TestAsyncUtils();
        mockDB.queryWithType(query, Emergency.class).thenApply(emergencies -> emergencies.get(0)).whenComplete((emergency, throwable) -> {
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


    private void submitEmergency(String category, String message) {
        onView(withId(R.id.buttonAskForHelp)).perform(click());
        onView(withId(R.id.spinnerEmergencyCategories)).perform(click());
        onView(withText(category)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.textEmergencyMessage)).perform(typeText(message)).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonEmergencySubmit)).perform(click());
    }

    @Override
    protected void setIds() {
        setIds(R.id.emergency_activity_drawer_layout, R.id.emergency_activity_nav_view);
    }
}
