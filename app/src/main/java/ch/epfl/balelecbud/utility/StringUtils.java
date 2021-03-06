package ch.epfl.balelecbud.utility;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import ch.epfl.balelecbud.R;

/**
 * Collection of methods used to work with Strings
 */
public final class StringUtils {

    public static boolean isEmailValid(Context context, EditText emailField) {
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError(context.getString(R.string.require_email));
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError(context.getString(R.string.invalid_email));
            return false;
        }
        emailField.setError(null);
        return true;
    }

    public static TextWatcher getTextWatcher(Runnable run) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                run.run();
            }
        };
    }
}
