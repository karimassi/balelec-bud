package ch.epfl.balelecbud.testUtils;

import android.widget.Switch;

import androidx.test.espresso.ViewAssertion;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.function.Function;

import ch.epfl.balelecbud.R;

import static org.hamcrest.Matchers.is;

public class CustomViewAssertion {
    public static ViewAssertion switchChecked(final boolean checked) {
        return getSwitchAssertion(Switch::isChecked, is(checked));
    }

    public static ViewAssertion switchClickable(final boolean isClick) {
        return getSwitchAssertion(Switch::isClickable, is(isClick));
    }

    @NotNull
    private static <T> ViewAssertion getSwitchAssertion(Function<Switch, T> actual, Matcher<? super T> matcher) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null)
                throw noViewFoundException;
            if (!(view instanceof Switch))
                throw new AssertionError("The View should be a Switch be was not");
            Assert.assertThat(actual.apply((Switch) view), matcher);
        };
    }
}
