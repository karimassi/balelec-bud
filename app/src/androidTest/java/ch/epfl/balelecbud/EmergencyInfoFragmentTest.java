package ch.epfl.balelecbud;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EmergencyInfoFragmentTest extends RootActivityTest{

    final EmergencyInfo info1 = new EmergencyInfo("To much alcool","Seek assistance");
    final EmergencyInfo info2 = new EmergencyInfo("Lost","Check your location on the map");
    private final MockDatabaseWrapper mock = MockDatabaseWrapper.getInstance();

    @Before
    public void setup() {
        mock.resetDocument(DatabaseWrapper.EMERGENCY_INFO_PATH);
    }

    /**@Before
    public void setup(){
        mock.resetDocument(DatabaseWrapper.EMERGENCY_INFO_PATH);
    }**/

    /**@Rule
    public final ActivityTestRule<RootActivity> mActivityRule = new ActivityTestRule<RootActivity>(RootActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppDatabaseWrapper(mock);
        }
    };**/

    @Override
    protected void setUpBeforeActivityLaunched() {
        super.setUpBeforeActivityLaunched();
        cleanUp();
        mock.resetDocument(DatabaseWrapper.EMERGENCY_INFO_PATH);
        BalelecbudApplication.setAppDatabaseWrapper(mock);
    }

    @After
    public void cleanUp() {
        mock.resetDocument(DatabaseWrapper.EMERGENCY_INFO_PATH);
    }

    @Test
    public void testEmergencyInfoRecyclerViewIsDisplayed() {
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddInfoToDatabase() {
        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info1);
        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());
        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
    }

    @Ignore("Currently modifying info does not make sens")
    @Test
    public void testCanModifyInfoFromDatabase() {
        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH,info1);
        mock.updateDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, 0, info2 );

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info2);
    }

    @Ignore("Currently modifying info does not make sens")
    @Test
    public void testCantModifyInfoFromDatabaseThatIsNotThere() {
        final EmergencyInfo emergencyInfoModified = new EmergencyInfo();

        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH,info1);
        mock.updateDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, 0, emergencyInfoModified);

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), emergencyInfoModified);
    }

    @Ignore
    @Test
    public void testCanDeleteInfoFromDatabase() {


        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info1);
        mock.storeDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(1)), info2);
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(hasChildCount(2)));

        mock.deleteDocument(DatabaseWrapper.EMERGENCY_INFO_PATH, info2);

        onView(withId(R.id.swipe_refresh_layout_emergency_info)).perform(swipeDown());

        testInfoInView(onView(new RecyclerViewMatcher(R.id.emergencyInfoRecyclerView).atPosition(0)), info1);
        onView(withId(R.id.emergencyInfoRecyclerView)).check(matches(hasChildCount(1)));
    }

    @Ignore("What this should do is unclear")
    @Test
    public void testCantDeleteInfoFromEmptyDatabase() {
        final EmergencyInfo info = new EmergencyInfo();
        mock.deleteDocument(DatabaseWrapper.EMERGENCY_INFO_PATH,info);
    }

    private void testInfoInView(ViewInteraction viewInteraction, EmergencyInfo information) {
        viewInteraction.check(matches(hasDescendant(withText(information.getName()))));
        viewInteraction.check(matches(hasDescendant(withText(information.getInstruction()))));
    }

    @Override
    protected int getItemId() {
        return R.id.activity_main_drawer_emergency_info;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.emergencyInfoRecyclerView;
    }
}
