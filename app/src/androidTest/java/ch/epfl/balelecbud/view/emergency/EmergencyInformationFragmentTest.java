package ch.epfl.balelecbud.view.emergency;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.EmergencyInformation;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.view.friendship.SocialFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class EmergencyInformationFragmentTest {

    private final EmergencyInformation info1 = new EmergencyInformation("Too much alcohol","Seek assistance", false);
    private final EmergencyInformation number1 = new EmergencyInformation("Police", "117", true);
    private final MockDatabase mock = MockDatabase.getInstance();

    @Before
    public void setup() {
        mock.resetDatabase();
        mock.resetDocument(Database.EMERGENCY_INFO_PATH);
        BalelecbudApplication.setAppDatabase(mock);
        mock.storeDocument(Database.EMERGENCY_INFO_PATH, info1);
        mock.storeDocument(Database.EMERGENCY_INFO_PATH, number1);
        FragmentScenario.launchInContainer(EmergencyInformationFragment.class, null, R.style.Theme_AppCompat, null);
    }

    @After
    public void cleanUp() {
        mock.resetDocument(Database.EMERGENCY_INFO_PATH);
    }

    @Test
    public void testEmergencyInfoRecyclerViewIsDisplayed() {
        onView(ViewMatchers.withId(R.id.recycler_view_emergency_info)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.recycler_view_emergency_numbers)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmergenciesDisplayed() {
        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.recycler_view_emergency_info).atPosition(0)), info1);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.recycler_view_emergency_numbers).atPosition(0)), number1);
    }

    @Test
    public void testCallEmergencyNumber() {
        Intents.init();
        Matcher expectedIntent = allOf(hasAction(Intent.ACTION_CALL), hasData("tel:" + number1.getInstruction()));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.recycler_view_emergency_numbers)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, click()));
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void emergencyButtonIsDisplayed() {
        onView(withId(R.id.fab_declare_emergency)).check(matches(isDisplayed()));
    }

    @Test
    public void emergencyDialogIsDisplayed() {
        onView(withId(R.id.fab_declare_emergency)).perform(click());
        onView(withId(R.id.edit_text_emergency_message)).check(matches(isDisplayed()));
    }

    private void testInfoInView(ViewInteraction viewInteraction, EmergencyInformation information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInstruction()))));
    }
}