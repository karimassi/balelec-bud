package ch.epfl.balelecbud.friendship;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AddFriendFragmentTest {

    @Test
    public void testEventFragment() {
        FragmentScenario.launchInContainer(AddFriendFragment.class);
        onView(withId(R.id.text_view_add_friend)).check(matches(isDisplayed()));
    }
}


    /**private final User currentUser = MockDatabase.karim;
    private final User otherUser = MockDatabase.celine;
    private final User newFriend = MockDatabase.axel;
    private final User requestedUser = MockDatabase.gaspard;
    private final Authenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDb = MockDatabase.getInstance();

    @Before
    public void setup() throws InterruptedException {
        FragmentScenario.launchInContainer(AddFriendFragment.class);
    }

    @Test
    public void testFirst(){
        onView(withId(R.id.text_view_add_friend)).check(matches(isDisplayed()));
    }

    /**
    @Test
    public void addFriendDialogDismissedOnCancel() {
        onView(withText(R.string.add_friend_cancel)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogInvalidEmail() {
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
        onView(withId(R.id.edit_text_email_add_friend)).perform(typeText(currentUser.getEmail()))
                .perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        //onView(withText(R.string.add_own_as_friend))
               // .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
              //  .check(matches(isDisplayed()));
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
    }

    @Test
    public void addFriendDialogValidEmail() throws InterruptedException {
        onView(withId(R.id.edit_text_email_add_friend))
                .perform(typeText(otherUser.getEmail())).perform(closeSoftKeyboard());
        onView(withText(R.string.add_friend_request)).perform(click());
        onView(withId(R.id.text_view_add_friend)).check(doesNotExist());
        TestAsyncUtils sync = new TestAsyncUtils();
        MyQuery query = new MyQuery(Database.FRIEND_REQUESTS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, otherUser.getUid()));
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
    }
    **/
