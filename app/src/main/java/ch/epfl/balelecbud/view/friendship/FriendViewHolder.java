package ch.epfl.balelecbud.view.friendship;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

final class FriendViewHolder extends RecyclerView.ViewHolder {

    final TextView friendName;
    final TextView friendEmail;
    final ImageButton deleteButton;

    FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        friendName = itemView.findViewById(R.id.text_view_friend_item_name);
        friendEmail = itemView.findViewById(R.id.text_view_friend_item_email);
        deleteButton = itemView.findViewById(R.id.buttonDeleteFriendItem);
    }
}

