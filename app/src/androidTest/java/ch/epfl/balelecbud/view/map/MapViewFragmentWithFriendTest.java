package ch.epfl.balelecbud.view.map;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.MarkerType;
import ch.epfl.balelecbud.model.MyMap;
import ch.epfl.balelecbud.model.MyMarker;
import ch.epfl.balelecbud.model.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.FriendshipUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;

import static ch.epfl.balelecbud.utility.database.MockDatabase.alex;
import static ch.epfl.balelecbud.utility.database.MockDatabase.celine;
import static ch.epfl.balelecbud.utility.database.MockDatabase.karim;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MapViewFragmentWithFriendTest {
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final Location karimLocation = new Location(2, 4);
    private final Location newKarimLocation = new Location(1, 2);
    private final Location alexLocation = new Location(3, 3);

    @Before
    public void setup() {
        MockDatabase.getInstance().resetDatabase();
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
        cleanUp();
        mockAuth.setCurrentUser(celine);
        FriendshipUtils.acceptRequest(karim);
        FriendshipUtils.acceptRequest(alex);
        mockDB.storeDocumentWithID(Database.LOCATIONS_PATH, karim.getUid(), karimLocation);
        MapViewFragment.setMockCallback(mapboxMap -> { });
    }

    @After
    public void cleanUp() {
        mockDB.resetDocument(Database.LOCATIONS_PATH);
        mockDB.resetDocument(Database.FRIENDSHIPS_PATH);
        mockDB.resetDocument(Database.FRIEND_REQUESTS_PATH);
    }

    @Test
    public void whenHaveOneFriendOneMarkerOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();

        MyMap mockMap = new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                assertNameAndLocation(markerBuilder, sync, karim, karimLocation);
                sync.call();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation, double zoom) {
                sync.call();
            }
        };

        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class);
        scenario.onFragment(fragment -> fragment.onMapReady(mockMap));

        sync.waitCall(2);
        sync.assertCalled(2);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenFriendLocationUpdateMarkerMoved() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();

        MyMap mockMap = new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                assertNameAndLocation(markerBuilder, sync, karim, karimLocation);
                sync.call();
                return location -> {
                    sync.call();
                    sync.assertEquals(newKarimLocation, location);
                };
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation, double zoom) {
                sync.call();
            }
        };

        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class);
        scenario.onFragment(fragment -> fragment.onMapReady(mockMap));

        sync.waitCall(2);
        mockDB.storeDocumentWithID(Database.LOCATIONS_PATH, karim.getUid(), newKarimLocation);
        sync.waitCall(3);
        sync.assertCalled(3);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenAFriendHaveNoLocationNothingIsShownOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();

        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class);
        scenario.onFragment(fragment -> fragment.onMapReady(assertKarimThenAlexLocation(sync)));

        sync.waitCall(2);
        mockDB.storeDocumentWithID(Database.LOCATIONS_PATH, alex.getUid(), alexLocation);
        sync.waitCall(3);
        sync.assertCalled(3);
        sync.assertNoFailedTests();
    }

    @NotNull
    private MyMap assertKarimThenAlexLocation(TestAsyncUtils sync) {
        return new MyMap() {
            private int times = 0;

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                if (times == 0) {
                    assertNameAndLocation(markerBuilder, sync, karim, karimLocation);
                } else {
                    assertNameAndLocation(markerBuilder, sync, alex, alexLocation);
                }
                sync.call();
                times += 1;
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation, double zoom) {
                sync.call();
            }
        };
    }

    private void assertNameAndLocation(MyMarker.Builder markerBuilder, TestAsyncUtils sync, User user, Location userLocation) {
        sync.assertEquals(user.getDisplayName(), markerBuilder.getTitle());
        sync.assertEquals(userLocation, markerBuilder.getLocation());
        sync.assertEquals(MarkerType.FRIEND, markerBuilder.getType());
    }

    @Test
    public void signOutClearListeners() {
        FragmentScenario scenario = FragmentScenario.launchInContainer(MapViewFragment.class);
        assertEquals(2, mockDB.getFriendsLocationListenerCount());
        scenario.moveToState(Lifecycle.State.DESTROYED);
        assertEquals(0, mockDB.getFriendsLocationListenerCount());
    }

}