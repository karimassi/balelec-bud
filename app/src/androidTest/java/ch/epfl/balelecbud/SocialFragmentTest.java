package ch.epfl.balelecbud;

import android.os.SystemClock;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.friendship.SocialFragment;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickChildViewWithId;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickTabWithPosition;

@RunWith(AndroidJUnit4.class)
public class SocialFragmentTest {

    private final User currentUser = MockDatabase.karim;
    private final User otherUser = MockDatabase.celine;
    private final User newFriend = MockDatabase.axel;
    private final User requestedUser = MockDatabase.gaspard;

    private final Authenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDb = MockDatabase.getInstance();

    @Before
    public void setup() {
        mockDb.resetDatabase();
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        BalelecbudApplication.setAppDatabase(mockDb);
        mockAuth.setCurrentUser(currentUser);
        mockDb.storeDocument(Database.USERS_PATH, newFriend);
        mockDb.resetDocument(Database.FRIENDSHIPS_PATH);
        mockDb.resetDocument(Database.FRIEND_REQUESTS_PATH);
        mockDb.resetDocument(Database.SENT_REQUESTS_PATH);
        createFriendship(otherUser);
        createRequest(newFriend, currentUser);
        createRequest(currentUser, requestedUser);


        FragmentScenario.launchInContainer(SocialFragment.class, null, R.style.Theme_AppCompat, null);

        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
    }

    private void selectAndRequest(int tab, User user1, User user2){
        selectTab(tab);
        createRequest(user1, user2);
    }

    private void selectAndCheck(int tab, int id, User user1){
        selectTab(tab);
        onView(withId(id)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(id).atPosition(0))
                .check(matches(hasDescendant(withText(user1.getDisplayName()))));
    }

    private void onTabClickOnChildAndSwipe(int tab, int recyclerViewId, int child, int layoutId) {
        selectTab(tab);
        onView(withId(recyclerViewId)).perform(RecyclerViewActions.
                actionOnItemAtPosition(0, clickChildViewWithId(child)));

        onView(withId(layoutId)).perform(swipeDown());
        onView(withId(recyclerViewId)).check(matches(hasChildCount(0)));
    }

    private void createFriendship(User user) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(user.getUid(), true);
        mockDb.storeDocumentWithID(Database.FRIENDSHIPS_PATH, currentUser.getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(currentUser.getUid(), true);
        mockDb.storeDocumentWithID(Database.FRIENDSHIPS_PATH, user.getUid(), toStore);
    }

    private void createRequest(User from, User to) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(from.getUid(), true);
        mockDb.storeDocumentWithID(Database.FRIEND_REQUESTS_PATH, to.getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(to.getUid(), true);
        getAppDatabase().storeDocumentWithID(Database.SENT_REQUESTS_PATH, from.getUid(), toStore);
    }

    private void selectTab(int position) {
        onView(withId(R.id.tabs_social)).perform(clickTabWithPosition(position));
        SystemClock.sleep(500);
    }

    @Test
    public void viewPagerShownTest() {
        onView(withId(R.id.view_pager_social)).check(matches(isDisplayed()));
    }

    @Test
    public void friendsShownTest() {
        selectAndCheck(0, R.id.recycler_view_friends, otherUser);

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
        onTabClickOnChildAndSwipe(0, R.id.recycler_view_friends, R.id.buttonDeleteFriendItem, R.id.swipe_refresh_layout_friends);
    }

    @Test
    public void receivedRequestsShownTest() {
        selectAndCheck(1,R.id.recycler_view_friend_requests, newFriend);
        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
    }

    @Test
    public void sentRequestsShownTest() {
        selectAndCheck(2,R.id.recycler_view_sent_request, requestedUser);
        onView(withId(R.id.swipe_refresh_layout_sent_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_sent_request)).check(matches(hasChildCount(1)));
    }

    @Test
    public void receivedRequestsAddedUpdatesListAfterRefresh() {
        selectAndRequest(1, otherUser, currentUser);

        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(0))
                .check(matches(hasDescendant(withText(newFriend.getDisplayName()))));

        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());

        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(1))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));
    }

    @Test
    public void sentRequestsAddedUpdatesListAfterRefresh() {
        selectAndRequest(2, currentUser, otherUser);

        onView(withId(R.id.recycler_view_sent_request)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_sent_request).atPosition(0))
                .check(matches(hasDescendant(withText(requestedUser.getDisplayName()))));

        onView(withId(R.id.swipe_refresh_layout_sent_requests)).perform(swipeDown());

        onView(new RecyclerViewMatcher(R.id.recycler_view_sent_request).atPosition(1))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));
    }

    private void deleteAndCheck(int tab, User user1, User user2, int id1, int id2){
        selectTab(tab);
        FriendshipUtils.deleteRequest(user1, user2);
        onView(withId(id1)).perform(swipeDown());
        onView(withId(id2)).check(matches(hasChildCount(0)));
    }

    @Test
    public void receivedRequestRemovedUpdatesListAfterRefresh() {
        deleteAndCheck(1, newFriend, currentUser, R.id.swipe_refresh_layout_friend_requests, R.id.recycler_view_friend_requests);
    }

    @Test
    public void sentRequestRemovedUpdatesListAfterRefresh() {
        deleteAndCheck(2, currentUser, requestedUser, R.id.swipe_refresh_layout_sent_requests, R.id.recycler_view_sent_request);
    }

    @Test
    public void buttonDeleteRequestUpdatesList() {
        onTabClickOnChildAndSwipe(1, R.id.recycler_view_friend_requests,
                R.id.button_request_item_delete_request, R.id.swipe_refresh_layout_friend_requests);
    }

    @Test
    public void buttonCancelRequestUpdatesList() {
        onTabClickOnChildAndSwipe(2, R.id.recycler_view_sent_request,
                R.id.button_sent_request_item_cancel, R.id.swipe_refresh_layout_sent_requests);
    }

    @Test
    public void buttonAcceptRequestUpdatesRequestsAndFriends() {
        onTabClickOnChildAndSwipe(1, R.id.recycler_view_friend_requests,
                R.id.button_request_item_accept_request, R.id.swipe_refresh_layout_friend_requests);

        selectTab(0);
        onView(withId(R.id.swipe_refresh_layout_friends)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friends)).check(matches(hasChildCount(2)));

    }

    @Test
    public void floatingActionButtonCreatesFragment() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(matches(isDisplayed()));
    }
}