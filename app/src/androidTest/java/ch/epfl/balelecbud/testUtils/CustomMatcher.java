package ch.epfl.balelecbud.testUtils;

import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NotNull;

import ch.epfl.balelecbud.R;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class CustomMatcher {
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
    public static Matcher<View> getItemInSchedule(int itemPosition, int infoPosition) {
        return nthChildOf(
                    nthChildOf(
                            nthChildOf(
                                nthChildOf(withId(R.id.scheduleRecyclerView), itemPosition),
                                    0),
                        1),
                    infoPosition);
    }
}
