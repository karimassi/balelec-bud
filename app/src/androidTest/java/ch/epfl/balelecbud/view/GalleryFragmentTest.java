package ch.epfl.balelecbud.view;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.storage.MockStorage;
import ch.epfl.balelecbud.view.emergency.EmergencyInformationFragment;
import ch.epfl.balelecbud.view.gallery.GalleryFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.CustomMatcher.nthChildOf;
import static ch.epfl.balelecbud.utility.storage.Storage.USER_PICTURES;

@RunWith(AndroidJUnit4.class)
public class GalleryFragmentTest {

    private FragmentScenario<GalleryFragment> scenario;
    private final MockStorage mockStorage = MockStorage.getInstance();

    @Rule
    public GrantPermissionRule permissionRuleCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);


    @Before
    public void setup() {
        mockStorage.resetStorage();
        mockStorage.setAccessCount(0);
        BalelecbudApplication.setRemoteStorage(mockStorage);
        scenario = FragmentScenario.launchInContainer(GalleryFragment.class, null, R.style.Theme_AppCompat, null);
    }

    @Test
    public void testRecyclerViewVisible() {
        onView(ViewMatchers.withId(R.id.gallery_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testLinearLayoutVisible(){
        onView(ViewMatchers.withId(R.id.fragment_gallery_linear_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testRefreshLayoutVisible(){
        onView(ViewMatchers.withId(R.id.swipe_refresh_layout_gallery)).check(matches(isDisplayed()));
    }

    @Test
    public void testConstraintLayoutVisible() {
        onView(ViewMatchers.withId(R.id.gallery_constraint_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testImagesAreRetrievedFromMockStorage(){
        refreshRecyclerView();
        onView(ViewMatchers.withId(R.id.gallery_recycler_view)).
        check(matches(hasChildCount(9)));
    }

    @Test
    public void testImagesAreBoundToView(){
        refreshRecyclerView();
        for(int i = 0 ; i < 9 ; ++i){
            onView(nthChildOf(withId(R.id.gallery_recycler_view), i)).check(matches(isDisplayed()));
        }
    }

    private void refreshRecyclerView() {
        onView(withId(R.id.swipe_refresh_layout_gallery)).perform(swipeDown());
    }


    @Test
    public void cameraShouldOpenWithButtonPressed() {
        Intents.init();
        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(ViewMatchers.withId(R.id.fab_upload_picture)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.android.camera2"));
        Intents.release();
    }

    @Test
    public void cameraIsSavingImageToStorage() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();

        Intents.init();

        Bundle bundle = new Bundle();
        bundle.putParcelable("IMG_DATA", Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8));
        Intent resultImg = new Intent();
        resultImg.putExtras(bundle);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultImg);
        intending(toPackage("com.android.camera2")).respondWith(result);
        onView(withId(R.id.fab_upload_picture)).perform(click());
        intended(toPackage("com.android.camera2"));

        scenario.onFragment(fragment -> {
            File file = null;
            try {
                String[] paths = fragment.getCurrentPhotoPath().split("/");
                file = mockStorage.getFile(USER_PICTURES + "/" + paths[paths.length-1]).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sync.assertNotNull(file);
            sync.call();
        });

        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        Intents.release();
    }


    @Test
    public void buttonIsDisplayed() {
        onView(withId(R.id.fab_upload_picture)).check(matches(isDisplayed()));
    }

    @Test
    public void cameraIsSavingImageToLocalStorage() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();
        Intents.init();
        Bundle bundle = new Bundle();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        bundle.putParcelable("IMG_DATA", bitmap );
        Intent resultImg = new Intent();
        resultImg.putExtras(bundle);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultImg);
        intending(toPackage("com.android.camera2")).respondWith(result);
        onView(withId(R.id.fab_upload_picture)).perform(click());
        intended(toPackage("com.android.camera2"));

        scenario.onFragment(fragment -> {
            File file = fragment.getActivity().getFilesDir();
            boolean found = false;
            for (File f : file.listFiles()) {
                if (f.getAbsolutePath().equals(fragment.getCurrentPhotoPath())) {
                    found = true;
                    break;
                }
            }
            sync.call();
            sync.assertTrue(found);
        });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        Intents.release();
    }

}
