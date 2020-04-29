package ch.epfl.balelecbud;

import android.Manifest;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RegisterUserActivityTest extends BasicAuthenticationTest {

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

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION
    );

    @Rule
    public final ActivityTestRule<RegisterUserActivity> mActivityRule =
            new ActivityTestRule<RegisterUserActivity>(RegisterUserActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    MockAuthenticator.getInstance().signOut();
                    Intents.init();
                }

                @Override
                protected void afterActivityFinished() {
                    Intents.release();
                }
            };

    @Before
    public void setUp() throws Throwable {
        BalelecbudApplication.setAppDatabaseWrapper(MockDatabaseWrapper.getInstance());
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        logout();
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
        enterValuesAndClick("name", "invalidemail", "124", "124");
        checkErrors(nameNoError, emailInvalidError, pwdTooShortError, pwdRepeatNoError);
    }

    @Test
    public void testRegisterExistingAccount() {
        enterValuesAndClick("name", "karim@epfl.ch", "123456", "123456");
        intended(hasComponent(RegisterUserActivity.class.getName()));
    }

    @Test
    public void testGoToLogin() {
        onView(withId(R.id.buttonRegisterToLogin)).perform(click());
        intended(hasComponent(LoginUserActivity.class.getName()));
    }

    @Test
    public void testCanRegister() {
        enterValuesAndClick("name", "testregister" + randomInt() + "@gmail.com", "123123", "123123");
        intended(hasComponent(WelcomeActivity.class.getName()));
    }

    @Test
    public void testCanRegisterFailDB() {
        BalelecbudApplication.setAppDatabaseWrapper(new DatabaseWrapper() {
            @Override
            public void unregisterDocumentListener(String collectionName, String documentID) { }

            @Override
            public <T> void listenDocument(String collectionName, String documentID, Consumer<T> consumer, Class<T> type) { }

            @Override
            public <T> CompletableFuture<List<T>> queryWithType(MyQuery query, Class<T> tClass) {
                return null;
            }

            @Override
            public CompletableFuture<List<Map<String, Object>>> query(MyQuery query) {
                return null;
            }

//            @Override
//            public <T> CompletableFuture<T> getCustomDocument(String collectionName, String documentID, Class<T> type) {
//                return CompletableFuture.completedFuture(null).thenCompose(o -> {
//                    throw new RuntimeException("Failed to store document");
//                });
//            }
//
//            @Override
//            public CompletableFuture<Map<String, Object>> getDocument(String collectionName, String documentID) {
//                return null;
//            }
//
//            @Override
//            public <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName,
//                                                                          String fieldName, String fieldValue, Class<T> type) {
//                return null;
//            }

            @Override
            public void updateDocument(String collectionName, String documentID, Map<String, Object> updates) {
            }

            @Override
            public <T> void storeDocument(String collectionName, T document) {
            }

            @Override
            public <T> CompletableFuture<Void> storeDocumentWithID(String collectionName, String documentID, T document) {
                return CompletableFuture.completedFuture(null).thenApply(o -> {
                    throw new RuntimeException("Failed to store document");
                });
            }

            @Override
            public void deleteDocumentWithID(String collectionName, String documentID) {
            }
        });
        enterValuesAndClick("name", "testregister" + randomInt() + "@gmail.com", "123123", "123123");
        intended(hasComponent(RegisterUserActivity.class.getName()));
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
        onView(withId(R.id.buttonRegister)).perform(click());
    }

    private String randomInt() {
        return String.valueOf(new Random().nextInt(10000));
    }

}