package ch.epfl.balelecbud;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.Emergency.EmergencyInfoActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class EmergencyInfoActivityTest {
    @Rule
    public final ActivityTestRule<EmergencyInfoActivity> mActivityRule =
            new ActivityTestRule<>(EmergencyInfoActivity.class);

    @Test
    public void testTextViewIsNonNull() {
        EmergencyInfoActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.ermergencyTextView);
        assertThat(viewById, notNullValue());
    }

    @Test
    public void testTextViewIsDisplayed() {
        onView(withId(R.id.ermergencyTextView)).check(matches(isDisplayed()));
    }
}