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
        BalelecbudApplication.setAppDatabaseWrapper(MockDatabaseWrapper.getInstance());
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        logout();
    }

    @Test
    public void testCantRegisterWithEmptyFields() {
        enterValues("", "", "", "");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Email required!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterInvalidEmailEmptyPassword() {
        enterValues("", "invalidemail", "", "");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterValidEmailEmptyPassword() {
        // valid email empty pwd
        enterValues("", "valid@email.com", "", "");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password required!")));
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Repeat password!")));
    }

    @Test
    public void testCantRegisterInvalidEmail() {
        // invalid email valid pwd
        enterValues("", "invalidemail", "123456", "123456");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(hasErrorText("Name required!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
    }

    @Test
    public void testCantRegisterMismatchPassword() {
        // invalid email valid pwd
        enterValues("name", "valid@email.com", "123456", "123478");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextRepeatPasswordRegister)).check(matches(hasErrorText("Passwords do not match!")));
        onView(withId(R.id.editTextEmailRegister)).check(matches(not(hasErrorText("Enter a valid email!"))));
    }

    @Test
    public void testCantRegisterInvalidEmailShortPassword() {
        // invalid email invalid password
        enterValues("name", "invalidemail", "124", "124");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withId(R.id.editTextNameRegister)).check(matches(not(hasErrorText("Name required!"))));
        onView(withId(R.id.editTextEmailRegister)).check(matches(hasErrorText("Enter a valid email!")));
        onView(withId(R.id.editTextPasswordRegister)).check(matches(hasErrorText("Password should be at least 6 characters long.")));
    }

    @Test
    public void testRegisterExistingAccount() {
        enterValues("name", "karim@epfl.ch", "123456", "123456");
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
        enterValues("name", "testregister" + randomInt() + "@gmail.com", "123123", "123123");
        onView(withId(R.id.buttonRegister)).perform(click());
        intended(hasComponent(WelcomeActivity.class.getName()));
    }

    @Test
    public void testCanRegisterFailDB() {
        enterValues("name", "testregister" + randomInt() + "@gmail.com", "123123", "123123");
        BalelecbudApplication.setAppDatabaseWrapper(new DatabaseWrapper() {
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
            public void deleteDocumentWithID(String collectionName, String documentID) {

            }
        });
        onView(withId(R.id.buttonRegister)).perform(click());
        intended(hasComponent(RegisterUserActivity.class.getName()));
    }

    private void enterValues(String name, String email, String pwd, String pwd2) {
        onView(withId(R.id.editTextNameRegister)).perform(typeText(name)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextEmailRegister)).perform(typeText(email)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordRegister)).perform(typeText(pwd)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextRepeatPasswordRegister)).perform(typeText(pwd2)).perform(closeSoftKeyboard());
    }

    private String randomInt() {
        return String.valueOf(new Random().nextInt(10000));
    }

}