package ch.epfl.balelecbud.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.balelecbud.R;

public class StringUtils {

    public static String timestampToScheduleString(Timestamp time) {
        Date date = time.toDate();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm");
        return dtf.format(date);
    }

    public static boolean isEmailValid(Context context, EditText emailField) {
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
}
