package ch.epfl.balelecbud;



import androidx.test.rule.ActivityTestRule;
import android.view.View;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;


@RunWith(AndroidJUnit4.class)
public class EmergencyNumbersActivityTest {
    @Rule
    public final ActivityTestRule<EmergencyNumbersActivity> mActivityRule =
            new ActivityTestRule<>(EmergencyNumbersActivity.class);

    @Test
    public void testListViewIsNonNull() {
        EmergencyNumbersActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.numbersListView);
        assertThat(viewById, notNullValue());
    }

    @Test
    public void testListViewIsDisplayed() {
        onView(withId(R.id.numbersListView)).check(matches(isDisplayed()));
    }
    
}

