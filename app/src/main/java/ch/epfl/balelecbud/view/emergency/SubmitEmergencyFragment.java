package ch.epfl.balelecbud.view.emergency;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Emergency;
import ch.epfl.balelecbud.model.EmergencyCategory;
import ch.epfl.balelecbud.utility.StringUtils;
import ch.epfl.balelecbud.utility.database.Database;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;

public final class SubmitEmergencyFragment extends DialogFragment {

    private static final String TAG = SubmitEmergencyFragment.class.getSimpleName();
    private EditText messageField;
    private Spinner categorySpinner;
    private TextWatcher watcher = StringUtils.getTextWatcher(() ->
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(validateEntry()));

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_emergency, null);
        Log.d(TAG, "onCreateDialog: view inflated");

        TextView title = new TextView(getContext());
        title.setText(R.string.emergency_ask_for_help);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setPaddingRelative(0, 10, 0, 10);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        messageField = view.findViewById(R.id.edit_text_emergency_message);
        messageField.addTextChangedListener(watcher);

        categorySpinner = view.findViewById(R.id.spinner_emergency_categories);
        categorySpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, EmergencyCategory.values()));

        builder.setView(view)
                .setCustomTitle(title)
                .setPositiveButton(R.string.submit_emergency, (dialog, id) -> {
                    if (validateEntry()) {
                        submitEmergency();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        view.post(() -> {
            AlertDialog dialog = ((AlertDialog) getDialog());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });

        return builder.create();
    }

    private boolean validateEntry() {
        String emergencyMessage = messageField.getText().toString();
        if (TextUtils.isEmpty(emergencyMessage)) {
            messageField.setError(getString(R.string.emergency_fill_message));
            return false;
        }
        return true;
    }

    private void submitEmergency() {
        String emergencyMessage = messageField.getText().toString();
        EmergencyCategory emergencyCategory = EmergencyCategory.valueOf(categorySpinner.getSelectedItem().toString().toUpperCase());
        Emergency emergency = new Emergency(emergencyCategory, emergencyMessage, getAppAuthenticator().getCurrentUid(), Timestamp.now());
        getAppDatabase().storeDocument(Database.EMERGENCIES_PATH, emergency);
        Toast.makeText(getActivity(), R.string.emergency_sent_message, Toast.LENGTH_SHORT).show();
    }

    public static SubmitEmergencyFragment newInstance() {
        SubmitEmergencyFragment f = new SubmitEmergencyFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

}
