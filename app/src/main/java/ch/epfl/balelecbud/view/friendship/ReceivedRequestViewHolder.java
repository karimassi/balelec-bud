package ch.epfl.balelecbud.view.friendship;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class ReceivedRequestViewHolder extends RecyclerView.ViewHolder {

    final TextView friendName;
    final TextView friendEmail;
    final ImageButton acceptButton;
    final ImageButton deleteButton;

    ReceivedRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        friendName = itemView.findViewById(R.id.text_view_friend_request_item_name);
        friendEmail = itemView.findViewById(R.id.text_view_friend_request_item_email);
        acceptButton = itemView.findViewById(R.id.button_request_item_accept_request);
        deleteButton = itemView.findViewById(R.id.button_request_item_delete_request);
    }
}