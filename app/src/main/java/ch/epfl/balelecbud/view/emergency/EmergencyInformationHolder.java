package ch.epfl.balelecbud.view.emergency;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class EmergencyInformationHolder extends RecyclerView.ViewHolder {

    public final TextView emergencyInfoNameTextView;
    public final TextView emergencyInfoInstructionTextView;

    public EmergencyInformationHolder(View view) {
        super(view);
        emergencyInfoNameTextView = view.findViewById(R.id.emergencyInfoName);
        emergencyInfoInstructionTextView = view.findViewById(R.id.emergencyInfoInstruction);
    }
}
