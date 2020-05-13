package ch.epfl.balelecbud.view.friendship;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class SentRequestViewHolder extends RecyclerView.ViewHolder {

    public final TextView friendName;
    public final Button cancelButton;

    public SentRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        friendName = itemView.findViewById(R.id.text_view_sent_request_item_name);
        cancelButton = itemView.findViewById(R.id.button_sent_request_item_cancel);
    }
}
