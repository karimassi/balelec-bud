package ch.epfl.balelecbud.view.friendship;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class ReceivedRequestViewHolder extends RecyclerView.ViewHolder {

    final TextView friendName;
    final Button acceptButton;
    final Button deleteButton;

    ReceivedRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        friendName = itemView.findViewById(R.id.text_view_friend_request_item_name);
        acceptButton = itemView.findViewById(R.id.button_request_item_accept_request);
        deleteButton = itemView.findViewById(R.id.button_request_item_delete_request);
    }
}