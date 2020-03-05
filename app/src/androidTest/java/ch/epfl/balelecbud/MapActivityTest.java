package ch.epfl.balelecbud;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    @Rule
    public final ActivityTestRule<MapActivity> mapActivityRule =
            new ActivityTestRule<>(MapActivity.class);
}
