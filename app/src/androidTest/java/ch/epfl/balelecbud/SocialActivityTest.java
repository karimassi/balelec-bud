package ch.epfl.balelecbud;


import android.os.SystemClock;
import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.RecyclerViewButtonClick;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.testUtils.TabLayoutTabSelect;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SocialActivityTest {

    User currentUser = new User("karim@epfl.ch", "karim@epfl.ch", "0");
    User otherUser = new User("celine@epfl.ch", "celine@epfl.ch", "1");
    User newFriend;

    Authenticator mockAuth = MockAuthenticator.getInstance();
    MockDatabaseWrapper mockDb = (MockDatabaseWrapper) MockDatabaseWrapper.getInstance();

    @Rule
    public final ActivityTestRule<SocialActivity> mActivityRule = new ActivityTestRule<SocialActivity>(SocialActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            SocialActivity.setAuthenticator(mockAuth);
            SocialActivity.setDatabase(mockDb);
            FriendshipUtils.setAuthenticator(mockAuth);
            FriendshipUtils.setDatabaseImplementation(mockDb);
            mockAuth.setCurrentUser(currentUser);
            newFriend = new User("test@gmail.com", "test@gmail.com", MockAuthenticator.provideUid());
            mockDb.storeDocument(DatabaseWrapper.USERS_PATH, newFriend);
        }
    };

    private void createFriendship(User user) {
        Map<String,Boolean> toStore= new HashMap<>();
        toStore.put(user.getUid(), true);
        mockDb.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, currentUser.getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(currentUser.getUid(), true);
        mockDb.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, user.getUid(), toStore);
    }

    private void createRequestFromUser(User user) {
        Map<String,Boolean> toStore= new HashMap<>();
        toStore.put(user.getUid(), true);
        mockDb.storeDocumentWithID(DatabaseWrapper.FRIEND_REQUESTS_PATH, currentUser.getUid(), toStore);
    }

    private void selectTab(int position) {
        onView(withId(R.id.tabs_social)).perform(TabLayoutTabSelect.clickTabWithPosition(position));
        SystemClock.sleep(500);
    }

    @Before
    public void setup() {
        mockDb.resetFriendshipsAndRequests();
        createFriendship(otherUser);
        createRequestFromUser(newFriend);
        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
    }


    @Test
    public void viewPagerShownTest() {
        onView(withId(R.id.view_pager_social)).check(matches(isDisplayed()));
    }

    @Test
    public void friendsShownTest() {
        selectTab(0);
        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friends).atPosition(0))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));
        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(1)));
    }

    @Test
    public void friendsAddedUpdatesListAfterRefresh() {
        selectTab(0);
        createFriendship(newFriend);

        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friends).atPosition(0))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));

        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());

        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(2)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friends).atPosition(1))
                .check(matches(hasDescendant(withText(newFriend.getDisplayName()))));
    }

    @Test
    public void friendsRemovedUpdatesListAfterRefresh() {
        selectTab(0);
        FriendshipUtils.removeFriend(otherUser);
        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(0)));
    }

    @Test
    public void buttonDeleteFriendsUpdatesList() {
        selectTab(0);
        onView(withId(R.id.recycler_view_friends)).perform(RecyclerViewActions.
            actionOnItemAtPosition(0, RecyclerViewButtonClick.clickChildViewWithId(R.id.buttonDeleteFriendItem)));

        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(0)));
    }

    @Test
    public void requestsShownTest() {
        selectTab(1);
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(0))
                .check(matches(hasDescendant(withText(newFriend.getDisplayName()))));
        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
    }


    @Test
    public void requestsAddedUpdatesListAfterRefresh() {
        selectTab(1);
        createRequestFromUser(otherUser);

        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(0))
                .check(matches(hasDescendant(withText(newFriend.getDisplayName()))));

        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());

        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(1))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));
    }

    @Test
    public void requestRemovedUpdatesListAfterRefresh() {
        selectTab(1);
        FriendshipUtils.deleteRequest(newFriend);
        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(0)));
    }

    @Test
    public void buttonDeleteRequestUpdatesList() {
        selectTab(1);
        onView(withId(R.id.recycler_view_friend_requests)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, RecyclerViewButtonClick.clickChildViewWithId(R.id.button_request_item_delete_request)));

        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(0)));
    }

    @Test
    public void buttonAcceptRequestUpdatesRequestsAndFriends() {
        selectTab(1);
        onView(withId(R.id.recycler_view_friend_requests)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, RecyclerViewButtonClick.clickChildViewWithId(R.id.button_request_item_accept_request)));

        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(0)));

        selectTab(0);
        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(2)));

    }

    @Test
    public void floatingActionButtonCreatesFragment() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(matches(isDisplayed()));
    }

    @Test
    public void addFriendDialogDismissedOnCancel() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withText(R.string.add_friend_cancel)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogInvalidEmail() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText("fakemail")).perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());

        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogOwnEmail() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText(currentUser.getEmail())).perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withText(R.string.add_own_as_friend))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogValidEmail() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText(otherUser.getEmail())).perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
        mockDb.getDocument(DatabaseWrapper.FRIEND_REQUESTS_PATH, otherUser.getUid()).whenComplete(new BiConsumer<Map<String, Object>, Throwable>() {
            @Override
            public void accept(Map<String, Object> stringObjectMap, Throwable throwable) {
                if (stringObjectMap != null) {
                    Assert.assertTrue(stringObjectMap.containsKey(currentUser.getUid()));
                }
                else {
                    Assert.fail();
                }
            }
        });
    }

    @Test
    public void testDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_info));
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));

    }

    @Test
    public void openScheduleActivityFromDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_schedule));
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openPOIActivityFromDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_poi));
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openMapActivityFromDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_map));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_transport));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void openSocialActivityFromDrawer(){
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_social));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs_social)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager_social)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_add_friends)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.social_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackPress(){
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.social_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.social_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }
}
