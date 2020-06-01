package ch.epfl.balelecbud.view;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import ch.epfl.balelecbud.R;

public abstract class CustomDialogFragment extends DialogFragment {
    private final int titleId;

    protected CustomDialogFragment(int titleId) {
        this.titleId = titleId;
    }

    protected TextView getDialogCustomTitle() {
        TextView title = new TextView(getContext());
        title.setText(this.titleId);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setPaddingRelative(0, 10, 0, 10);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        return title;
    }
}
