package ch.epfl.balelecbud;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.schedule.ScheduleActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ScheduleActivityTest {
    //@Rule
    //public ActivityTestRule<ScheduleActivity> activityRule = new ActivityTestRule<>(ScheduleActivity.class, false, true);

    @Rule
    public ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<ScheduleActivity>(ScheduleActivity.class, false, true) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, ScheduleActivity.class);
                    result.putExtra("db", "mock");
                    return result;
                }

                @Override
                public ScheduleActivity launchActivity(@Nullable Intent startIntent) {
                    return super.launchActivity(getActivityIntent());
                }
            };


    @Test
    public void testRecyclerViewVisible() {
        onView(withId(R.id.rvSchedule))
                .inRoot(RootMatchers.withDecorView(
                        Matchers.is(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCaseForRecyclerItemView() {
        onView(withId(R.id.rvSchedule))
                .inRoot(RootMatchers.withDecorView(
                        Matchers.is(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(withViewAtPosition(0, Matchers.allOf(
                        withId(R.id.item_schedule), isDisplayed()))));
    }

    @Test
    public void testCaseForRecyclerItems() {
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 0)).check(matches(withText("19h - 20h")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 1)).check(matches(withText("Mr Oizo")));
        onView(nthChildOf(nthChildOf(withId(R.id.rvSchedule), 0), 2)).check(matches(withText("Grande scène")));
    }

    public Matcher<View> withViewAtPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
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
}
