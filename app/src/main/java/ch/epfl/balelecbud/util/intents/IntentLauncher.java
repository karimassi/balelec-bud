package ch.epfl.balelecbud.util.intents;

import android.content.Intent;

import androidx.annotation.NonNull;

public interface IntentLauncher {
    void launchIntent(@NonNull Intent intent);
}