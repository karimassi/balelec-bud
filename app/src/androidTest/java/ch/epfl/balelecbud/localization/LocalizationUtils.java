package ch.epfl.balelecbud.localization;

import android.Manifest;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

public class LocalizationUtils {
    public static void grantPermissions() {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + ApplicationProvider.getApplicationContext().getPackageName() + " "
                        + Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + ApplicationProvider.getApplicationContext().getPackageName() + " "
                            + Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    }

    public static void revokePermissions() {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "pm revoke " + ApplicationProvider.getApplicationContext().getPackageName() + " "
                        + Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm revoke " + ApplicationProvider.getApplicationContext().getPackageName() + " "
                            + Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    }
}
