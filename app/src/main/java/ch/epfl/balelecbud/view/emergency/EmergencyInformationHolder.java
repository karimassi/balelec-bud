package ch.epfl.balelecbud.view.emergency;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class EmergencyInformationHolder extends RecyclerView.ViewHolder {

    final TextView emergencyInfoNameTextView;
    final TextView emergencyInfoInstructionTextView;

    EmergencyInformationHolder(View view) {
        super(view);
        emergencyInfoNameTextView = view.findViewById(R.id.emergencyInfoName);
        emergencyInfoInstructionTextView = view.findViewById(R.id.emergencyInfoInstruction);
    }
}
