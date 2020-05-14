package ch.epfl.balelecbud;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static android.app.Instrumentation.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PicturesFragmentTest {
    private static UiDevice mDevice;
    private static final int CAMERA_REQUEST_CODE = 102;




    @Before
    public void setUp(){
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        FragmentScenario.launchInContainer(PicturesFragment.class);
    }

    @Test
    public void cameraShouldOpenWithButtonPressed() {
        Intents.init();
        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(withId(R.id.takePicBtn)).perform(click());
        allowPermissionsIfNeeded();

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.android.camera2"));
        Intents.release();
    }

    @Test
    public void cameraIsSavingImageToView() throws InterruptedException {
        TestAsyncUtils sync = new TestAsyncUtils();

        Intents.init();

        Bundle bundle = new Bundle();
        bundle.putParcelable("IMG_DATA", Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8));
        Intent resultImg = new Intent();
        resultImg.putExtras(bundle);
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultImg);

        onView(withId(R.id.takePicBtn)).perform(click());
        allowPermissionsIfNeeded();

        intending(toPackage("com.android.camera2")).respondWith(result);

        FragmentScenario.launchInContainer(PicturesFragment.class).onFragment(fragment -> {
            ImageView view = fragment.getActivity().findViewById(R.id.picturesImageView);
            Drawable drawable = view.getDrawable();
            boolean hasImage = (drawable != null);
            if (hasImage && (drawable instanceof BitmapDrawable)) {
                hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
            }
            sync.call();
            sync.assertTrue(hasImage);
        });

        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();

        Intents.release();
    }


    private static void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mDevice.hasObject(By.text("ALLOW"))){
                mDevice.findObject(By.text("ALLOW")).click();
            }
        }
    }


    @Test
    public void buttonIsDisplayed() {
        onView(withId(R.id.takePicBtn)).check(matches(isDisplayed()));
    }

}
