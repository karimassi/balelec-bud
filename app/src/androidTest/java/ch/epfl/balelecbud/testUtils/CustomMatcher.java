package ch.epfl.balelecbud.testUtils;

import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import ch.epfl.balelecbud.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class CustomMatcher {
    public static void clickAndCheckDisplay(List<Integer> idsToClick,
                                            List<Integer> idsToCheckDisplayed,
                                            List<Integer> idsToCheckNotDisplayed) {
        idsToClick.forEach(id -> onView(withText(id)).perform(click()));
        idsToCheckDisplayed.forEach(id -> onView(withText(id)).check(matches(isDisplayed())));
        idsToCheckNotDisplayed.forEach(id -> onView(withText(id)).check(doesNotExist()));
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + childPosition + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }

    @NotNull
    public static Matcher<View> getItemInSchedule(int itemPosition, int subPosition, int infoPosition) {
        return nthChildOf(
                    nthChildOf(
                            nthChildOf(withId(R.id.scheduleRecyclerView), itemPosition),
                        subPosition),
                    infoPosition);
    }

    /**
     * Matcher that is Toast window.
     * Taken from http://baroqueworksdev.blogspot.com/2015/03/how-to-check-toast-window-on-android.html
     */
    @NonNull
    public static Matcher<Root> isToast() {
        return new TypeSafeMatcher<Root>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("is toast");
            }

            @Override
            public boolean matchesSafely(Root root) {
                int type = root.getWindowLayoutParams().get().type;
                if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                    IBinder windowToken = root.getDecorView().getWindowToken();
                    IBinder appToken = root.getDecorView().getApplicationWindowToken();
                    if (windowToken == appToken) {
                        // windowToken == appToken means this window isn't contained by any other windows.
                        // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
