package ch.epfl.balelecbud;

import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public abstract class BasicActivityTest {

    private int activity_drawer_layout_id;
    private int activity_nav_view_id;


    @Before
    public void  passIDsToBasicActivityTest(){
        setIds();
    }

    @Test
    public void testDrawer() {
        openDrawer();
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_info, R.id.festivalInfoRecyclerView);
    }

    @Test
    public void openScheduleActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_schedule, R.id.scheduleRecyclerView);
    }

    @Test
    public void openPOIActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_poi, R.id.pointOfInterestRecyclerView);
    }

    @Test
    public void openMapActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_map, R.id.map);
    }

    @Test
    public void openTransportActivityFromDrawer() {
        openDrawer();
        clickItem(R.id.activity_main_drawer_transport, R.id.fragmentTransportList);
    }

    @Ignore("ignore for now because since stuff is not mocked this fails, could create hacky fix now " +
            "but will do something nice later instead")
    @Test
    public void openSocialActivityFromDrawer(){
        openDrawer();
        clickItem(R.id.activity_main_drawer_social, R.id.tabs_social);
    }

    @Test
    public void signOutFromDrawer() {
        openDrawer();
        onView(withId(activity_nav_view_id)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackPress(){
        openDrawer();
        Espresso.pressBack();
        onView(withId(activity_drawer_layout_id)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }

    private void openDrawer() {
        onView(withId(activity_drawer_layout_id)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(activity_nav_view_id)).check(matches(isDisplayed()));
    }

    private void clickItem(int itemId, int viewToDisplayId) {
        onView(withId(activity_nav_view_id)).perform(NavigationViewActions.navigateTo(itemId));
        onView(withId(viewToDisplayId)).check(matches(isDisplayed()));
    }

    protected abstract void setIds();

    protected void setIds(int activity_drawer_layout_id, int activity_nav_view_id){
        this.activity_drawer_layout_id = activity_drawer_layout_id;
        this.activity_nav_view_id = activity_nav_view_id;
    }


}
