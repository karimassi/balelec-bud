package ch.epfl.balelecbud.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.balelecbud.BasicIdlingResource;

public class BasicActivity extends AppCompatActivity {

    @Nullable protected BasicIdlingResource idlingResource;

    @VisibleForTesting
    @NonNull
    public BasicIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new BasicIdlingResource();
        }
        return idlingResource;
    }

}
