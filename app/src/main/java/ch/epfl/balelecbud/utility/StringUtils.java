package ch.epfl.balelecbud.utility;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.epfl.balelecbud.R;

public class StringUtils {

    public static String timestampToScheduleString(Timestamp time) {
        Date date = time.toDate();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return dtf.format(date);
    }

    public static boolean isEmailValid(Context context, EditText emailField) {
        emailField.setError(null);
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError(context.getString(R.string.require_email));
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError(context.getString(R.string.invalid_email));
            return false;
        }
        return true;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm", Locale.ENGLISH);
        return dateFormat.format(date);
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
