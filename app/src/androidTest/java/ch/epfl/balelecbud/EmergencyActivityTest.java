package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import ch.epfl.balelecbud.models.emergency.Emergency;
import ch.epfl.balelecbud.models.emergency.EmergencyType;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class EmergencyActivityTest {

    MockDatabaseWrapper mock;
//    private Emergency emergency = new Emergency(EmergencyType.THEFT, "Help please","a user id",new Timestamp(0, 0));

    @Rule
    public final ActivityTestRule<EmergencyActivity> mActivityRule =
            new ActivityTestRule<EmergencyActivity>(EmergencyActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    mock = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();
                }
            };

    @Test
    public void testSubmittEmergencyButtonIsDisplayedWhenButtonClicked() {
        onView(withId(R.id.buttonAskForHelp)).perform(click());
        onView(withId(R.id.buttonEmergencySubmit)).check(matches(isDisplayed()));

    }

    @Test
    public void buttonIsDisplayed() {
        EmergencyActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.buttonAskForHelp);
        assertThat(viewById, notNullValue());
    }


    private void submitEmergency(String category, String message) {
        onView(withId(R.id.buttonAskForHelp)).perform(click());
        onView(withId(R.id.spinnerEmergencyCategories)).perform(click());
        onView(allOf(withId(R.id.textEmergencyMessage), withText("Theft"))).perform(click());
        onView(withId(R.id.textEmergencyMessage)).perform(typeText(message)).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonEmergencySubmit)).perform(click());



    }

}
