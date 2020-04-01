package ch.epfl.balelecbud;

import android.Manifest;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.util.database.DatabaseListener;
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
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RegisterUserActivityTest extends BasicAuthenticationTest {

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION
    );

    @Rule
    public final ActivityTestRule<RegisterUserActivity> mActivityRule = new ActivityTestRule<RegisterUserActivity>(RegisterUserActivity.class) {
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
        RegisterUserActivity.setDatabase(MockDatabaseWrapper.getInstance());
        RegisterUserActivity.setAuthenticator(MockAuthenticator.getInstance());
        logout();
    }

    @Test
    public void testCantRegisterWithEmptyFields() {
        enterName("");
        enterEmail("");
        enterPassword("");
        repeatPassword("");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterInvalidEmailEmptyPassword() {
        enterName("");
        enterEmail("invalidemail");
        enterPassword("");
        repeatPassword("");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterValidEmailEmptyPassword() {
        // valid email empty pwd
        enterName("");
        enterEmail("valid@email.com");
        enterPassword("");
        repeatPassword("");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterInvalidEmail() {
        // invalid email valid pwd
        enterName("");
        enterEmail("invalidemail");
        enterPassword("123456");
        repeatPassword("123456");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
    }

    @Test
    public void testCantRegisterMismatchPassword() {
        // invalid email valid pwd
        enterName("name");
        enterEmail("valid@email.com");
        enterPassword("123456");
        repeatPassword("123478");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Passwords do not match!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(not(hasErrorText("Enter a valid email!"))));
    }

    @Test
    public void testCantRegisterInvalidEmailShortPassword() {
        // invalid email invalid password
        enterName("name");
        enterEmail("invalidemail");
        enterPassword("124");
        repeatPassword("124");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(not(hasErrorText("Name required!"))));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password should be at least 6 characters long.")));
    }

    @Test
    public void testRegisterExistingAccount() {
        enterName("name");
        enterEmail("karim@epfl.ch");
        enterPassword("123456");
        repeatPassword("123456");
        onView(withId(R.id.buttonRegister)).perform(click());
        intended(hasComponent(RegisterUserActivity.class.getName()));
    }

    @Test
    public void testGoToLogin() {
        onView(withId(R.id.buttonRegisterToLogin)).perform(click());
        intended(hasComponent(LoginUserActivity.class.getName()));
    }

    @Test
    public void testCanRegister() {
        enterName("name");
        enterEmail("testregister" + randomInt() + "@gmail.com");
        enterPassword("123123");
        repeatPassword("123123");
        onView(withId(R.id.buttonRegister)).perform(click());
        intended(hasComponent(WelcomeActivity.class.getName()));
    }

    @Test
    public void testCanRegisterFailDB() {
        enterName("name");
        enterEmail("testregister" + randomInt() + "@gmail.com");
        enterPassword("123123");
        repeatPassword("123123");
        RegisterUserActivity.setDatabase(new DatabaseWrapper() {
            @Override
            public void unregisterListener(DatabaseListener listener) {

            }

            @Override
            public void listen(String collectionName, DatabaseListener listener) {

            }

            @Override 
            public <T> CompletableFuture<List<T>> query(MyQuery query, Class<T> tClass) {
                return null;
            }
            
            @Override
            public <T> CompletableFuture<T> getCustomDocument(String collectionName, String documentID, Class<T> type) {
                return CompletableFuture.completedFuture(null).thenCompose(o -> {
                    throw new RuntimeException("Failed to store document");
                });
            }

            @Override
            public CompletableFuture<Map<String, Object>> getDocument(String collectionName, String documentID) {
                return null;
            }

            @Override
            public <T> CompletableFuture<T> getDocumentWithFieldCondition(String collectionName, String fieldName, String fieldValue, Class<T> type) {
                return null;
            }

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
            public void deleteDocument(String collectionName, String documentID) {

            }
        });
        onView(withId(R.id.buttonRegister)).perform(click());
        intended(hasComponent(RegisterUserActivity.class.getName()));
    }

    private void enterName(String name) {
        onView(withId(R.id.editTextNameRegister)).perform(typeText(name)).perform(closeSoftKeyboard());
    }

    private void enterEmail(String email) {
        onView(withId(R.id.editTextEmailRegister)).perform(typeText(email)).perform(closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText(password)).perform(closeSoftKeyboard());
    }

    private void repeatPassword(String password) {
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText(password)).perform(closeSoftKeyboard());
    }

    private String randomInt() {
        return String.valueOf(new Random().nextInt(10000));
    }

}