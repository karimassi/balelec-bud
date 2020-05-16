package ch.epfl.balelecbud.view.settings;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RegisterUserTest {

    private final Matcher<View> nameRequiredError = hasErrorText("Name required!");
    private final Matcher<View> emailRequiredError = hasErrorText("Email required!");
    private final Matcher<View> emailInvalidError = hasErrorText("Enter a valid email!");
    private final Matcher<View> pwdRequiredError = hasErrorText("Password required!");
    private final Matcher<View> pwdTooShortError = hasErrorText("Password should be at least 6 characters long.");
    private final Matcher<View> pwdRepeatRequiredError = hasErrorText("Repeat password!");
    private final Matcher<View> pwdsDoNotMatchError = hasErrorText("Passwords do not match!");
    private final Matcher<View> nameNoError = not(nameRequiredError);
    private final Matcher<View> emailNoError = not(anyOf(emailInvalidError, emailInvalidError));
    private final Matcher<View> pwdNoError = not(anyOf(pwdRequiredError, pwdTooShortError, pwdsDoNotMatchError));
    private final Matcher<View> pwdRepeatNoError = not(anyOf(pwdRepeatRequiredError, pwdsDoNotMatchError));

    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDB = MockDatabase.getInstance();

    @Before
    public void setUp() {
        BalelecbudApplication.setAppAuthenticator(mockAuth);
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
        mockAuth.signOut();
        FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.Theme_AppCompat, null);
        onView(withText(R.string.not_sign_in)).perform(click());
        onView(withText(R.string.action_no_account)).perform(click());
    }

    @Test
    public void testCantRegisterWithEmptyFields() {
        enterValuesAndClick("", "", "", "");
        checkErrors(nameRequiredError, emailRequiredError, pwdRequiredError, pwdRepeatRequiredError);
    }

    @Test
    public void testCantRegisterInvalidEmailEmptyPassword() {
        enterValuesAndClick("", "invalidemail", "", "");
        checkErrors(nameRequiredError, emailInvalidError, pwdRequiredError, pwdRepeatRequiredError);
    }

    @Test
    public void testCantRegisterValidEmailEmptyPassword() {
        // valid email empty pwd
        enterValuesAndClick("", "valid@email.com", "", "");
        checkErrors(nameRequiredError, emailNoError, pwdRequiredError, pwdRepeatRequiredError);
    }

    @Test
    public void testCantRegisterInvalidEmail() {
        // invalid email valid pwd
        enterValuesAndClick("", "invalidemail", "123456", "123456");
        checkErrors(nameRequiredError, emailInvalidError, pwdNoError, pwdRepeatNoError);
    }

    @Test
    public void testCantRegisterMismatchPassword() {
        // invalid email valid pwd
        enterValuesAndClick("name", "valid@email.com", "123456", "123478");
        checkErrors(nameNoError, emailNoError, pwdsDoNotMatchError, pwdsDoNotMatchError);
    }

    @Test
    public void testCantRegisterInvalidEmailShortPassword() {
        // invalid email invalid password
        enterValuesAndClick("name", "invalidemail", "124", "");
        checkErrors(nameNoError, emailInvalidError, pwdTooShortError, pwdRepeatRequiredError);
    }

    @Test
    public void testRegisterExistingAccount() {
        enterValuesAndClick("name", "karim@epfl.ch", "123456", "123456");
        onView(withText(R.string.not_sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToLogin() {
        onView(withText(R.string.action_existing_account)).perform(click());
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanRegister() {
        enterValuesAndClick("name", "testregister" + randomInt() + "@gmail.com", "123123", "123123");
        onView(withText(R.string.sign_out_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterSavesUserOnDB() throws Throwable {
        String email = "testregister" + randomInt() + "@gmail.com";
        TestAsyncUtils sync = new TestAsyncUtils();
        enterValuesAndClick("name", email, "123123", "123123");

        MyQuery query = new MyQuery(Database.USERS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, MockAuthenticator.getInstance().getCurrentUid()));
        mockDB.query(query, User.class).whenComplete((users, throwable) -> {
            if (throwable == null) {
                sync.assertEquals(email, users.get(0).getEmail());
                sync.assertEquals("name", users.get(0).getDisplayName());
                sync.call();
            } else {
                sync.fail();
            }
        });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void testCanRegisterFailDB() {
        BalelecbudApplication.setAppDatabase(new Database() {
            @Override
            public void unregisterDocumentListener(String collectionName, String documentID) { }
            @Override
            public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) { }
            @Override
            public <T> CompletableFuture<List<T>> query(MyQuery query, Class<T> tClass) { return null; }
            @Override
            public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) { return null; }
            @Override
            public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) { }
            @Override
            public <T> void storeDocument(String collectionName, T document) { }
            @Override
            public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
                return TestAsyncUtils.getExceptionalFuture("Failed to store document");
            }
            @Override
            public void deleteDocumentWithID(String collectionName, String documentID) { }
        });
        enterValuesAndClick("name", "testregister" + randomInt() + "@gmail.com", "123123", "123123");
        onView(withText(R.string.not_sign_in)).check(matches(isDisplayed()));
    }

    private void checkErrors(Matcher<View> nameMatcher,
                             Matcher<View> emailMatcher,
                             Matcher<View> pwdMatcher,
                             Matcher<View> pwd2Matcher) {
        onView(withId(R.id.editTextNameRegister)).check(matches(nameMatcher));
        onView(withId(R.id.editTextEmailRegister)).check(matches(emailMatcher));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(pwdMatcher));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(pwd2Matcher));
    }

    private void enterValuesAndClick(String name, String email, String pwd, String pwd2) {
        onView(withId(R.id.editTextNameRegister)).perform(typeText(name)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextEmailRegister)).perform(typeText(email)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText(pwd)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText(pwd2)).perform(closeSoftKeyboard());
        onView(withText(R.string.action_register)).perform(click());
    }

    private String randomInt() {
        return String.valueOf(new Random().nextInt(10000));
    }

}