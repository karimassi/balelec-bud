package ch.epfl.balelecbud;

import android.os.SystemClock;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.friendship.SocialActivity;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.RecyclerViewMatcher;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.database.MyWhereClause;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickChildViewWithId;
import static ch.epfl.balelecbud.testUtils.CustomViewAction.clickTabWithPosition;
import static ch.epfl.balelecbud.util.database.DatabaseWrapper.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.util.database.MyWhereClause.Operator.EQUAL;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SocialActivityTest extends BasicActivityTest {

    private final User currentUser = MockDatabaseWrapper.karim;
    private final User otherUser = MockDatabaseWrapper.celine;
    private final User newFriend = MockDatabaseWrapper.axel;
    private final User requestedUser = MockDatabaseWrapper.gaspard;

    private final Authenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabaseWrapper mockDb = MockDatabaseWrapper.getInstance();

    @Rule
    public final ActivityTestRule<SocialActivity> mActivityRule = new ActivityTestRule<SocialActivity>(SocialActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            BalelecbudApplication.setAppAuthenticator(mockAuth);
            BalelecbudApplication.setAppDatabaseWrapper(mockDb);
            mockAuth.setCurrentUser(currentUser);
            mockDb.storeDocument(DatabaseWrapper.USERS_PATH, newFriend);
        }
    };

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
        mockDb.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, currentUser.getUid(), toStore);

        toStore = new HashMap<>();
        toStore.put(currentUser.getUid(), true);
        mockDb.storeDocumentWithID(DatabaseWrapper.FRIENDSHIPS_PATH, user.getUid(), toStore);
    }

    private void createRequest(User from, User to) {
        Map<String, Boolean> toStore = new HashMap<>();
        toStore.put(from.getUid(), true);
        mockDb.storeDocumentWithID(DatabaseWrapper.FRIEND_REQUESTS_PATH, to.getUid(), toStore);
    }

    private void selectTab(int position) {
        onView(withId(R.id.tabs_social)).perform(clickTabWithPosition(position));
        SystemClock.sleep(500);
    }

    @Before
    public void setup() {
        mockDb.resetDocument(DatabaseWrapper.FRIENDSHIPS_PATH);
        mockDb.resetDocument(DatabaseWrapper.FRIEND_REQUESTS_PATH);
        createFriendship(otherUser);
        createRequest(newFriend, currentUser);
        createRequest(currentUser, requestedUser);
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
        onTabClickOnChildAndSwipe(0, R.id.recycler_view_friends, R.id.buttonDeleteFriendItem, R.id.swipe_refresh_layout_friends);
    }

    @Test
    public void receivedRequestsShownTest() {
        selectTab(1);
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(0))
                .check(matches(hasDescendant(withText(newFriend.getDisplayName()))));
        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
    }

    @Test
    @Ignore
    public void sentRequestsShownTest() {
        selectTab(2);
        onView(withId(R.id.recycler_view_sent_request)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_sent_request).atPosition(0))
                .check(matches(hasDescendant(withText(requestedUser.getDisplayName()))));
        onView(withId(R.id.swipe_refresh_layout_sent_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_sent_request)).check(matches(hasChildCount(1)));
    }

    @Test
    public void receivedRequestsAddedUpdatesListAfterRefresh() {
        selectTab(1);
        createRequest(otherUser, currentUser);

        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(0))
                .check(matches(hasDescendant(withText(newFriend.getDisplayName()))));

        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());

        onView(new RecyclerViewMatcher(R.id.recycler_view_friend_requests).atPosition(1))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));
    }

    @Test
    @Ignore
    public void sentRequestsAddedUpdatesListAfterRefresh() {
        selectTab(2);
        createRequest(currentUser, otherUser);

        onView(withId(R.id.recycler_view_sent_request)).check(matches(hasChildCount(1)));
        onView(new RecyclerViewMatcher(R.id.recycler_view_sent_request).atPosition(0))
                .check(matches(hasDescendant(withText(requestedUser.getDisplayName()))));

        onView(withId(R.id.swipe_refresh_layout_sent_requests)).perform(swipeDown());

        onView(new RecyclerViewMatcher(R.id.recycler_view_sent_request).atPosition(1))
                .check(matches(hasDescendant(withText(otherUser.getDisplayName()))));
    }


    @Test
    public void receivedRequestRemovedUpdatesListAfterRefresh() {
        selectTab(1);
        FriendshipUtils.deleteRequest(newFriend, currentUser);
        onView(withId(R.id.swipe_refresh_layout_friend_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_friend_requests)).check(matches(hasChildCount(0)));
    }

    @Test
    @Ignore
    public void sentRequestRemovedUpdatesListAfterRefresh() {
        selectTab(2);
        FriendshipUtils.deleteRequest(currentUser, requestedUser);
        onView(withId(R.id.swipe_refresh_layout_sent_requests)).perform(swipeDown());
        onView(withId(R.id.recycler_view_sent_request)).check(matches(hasChildCount(0)));
    }

    @Test
    public void buttonDeleteRequestUpdatesList() {
        onTabClickOnChildAndSwipe(1, R.id.recycler_view_friend_requests,
                R.id.button_request_item_delete_request, R.id.swipe_refresh_layout_friend_requests);
    }

    @Test
    @Ignore
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

    @Test
    public void addFriendDialogDismissedOnCancel() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withText(R.string.add_friend_cancel)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogInvalidEmail() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText("fakemail"))
                .perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());

        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText(""))
                .perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogOwnEmail() {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText(currentUser.getEmail()))
                .perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withText(R.string.add_own_as_friend))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogValidEmail() throws InterruptedException {
        onView(withId(R.id.fab_add_friends)).perform(click());
        onView(withId(R.id.edit_text_email_add_friend))
                .perform(typeText(otherUser.getEmail())).perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
        TestAsyncUtils sync = new TestAsyncUtils();
        MyQuery query = new MyQuery(DatabaseWrapper.FRIEND_REQUESTS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, otherUser.getUid()));
        mockDb.query(query).thenApply(maps -> maps.get(0))
                .whenComplete((stringObjectMap, throwable) -> {
                    if (stringObjectMap != null) {
                        sync.assertTrue(stringObjectMap.containsKey(currentUser.getUid()));
                    } else {
                        sync.fail();
                    }
                    sync.call();
                });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Override
    protected void setIds() {
        setIds(R.id.social_activity_drawer_layout, R.id.social_activity_nav_view);
    }
}
