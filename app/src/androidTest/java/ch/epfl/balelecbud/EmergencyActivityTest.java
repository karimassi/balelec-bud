package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class EmergencyActivityTest {


    private final User user = MockDatabaseWrapper.karim;

//    private Emergency emergency = new Emergency(EmergencyType.THEFT, "Help please","a user id",new Timestamp(0, 0));

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
    public void setup(){
        MockDatabaseWrapper.getInstance().resetDocument(DatabaseWrapper.EMERGENCIES_PATH);
        MockAuthenticator.getInstance().signOut();
        MockAuthenticator.getInstance().setCurrentUser(user);
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

        submitEmergency("Theft","I lost something");
        //submitEmergency("Faintness","I don't feel well");

        Emergency res = MockDatabaseWrapper.getInstance().getDocumentWithFieldCondition(DatabaseWrapper.EMERGENCIES_PATH, "category", "Theft", Emergency.class).get();
//        Emergency res2 = mock.getDocumentWithFieldCondition(DatabaseWrapper.EMERGENCIES_PATH, "category", "Theft", Emergency.class).get();
        assertThat(res, notNullValue());
//        assertThat(res2, notNullValue());
        assertEquals(res.getMessage(),"I lost something" );
//        assertEquals(res2.getMessage(),"I don't feel well" );
    }


    private void submitEmergency(String category, String message) {
        onView(withId(R.id.buttonAskForHelp)).perform(click());
        onView(withId(R.id.spinnerEmergencyCategories)).perform(click());
        onView(withText(category)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.textEmergencyMessage)).perform(typeText(message)).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonEmergencySubmit)).perform(click());
    }

}
