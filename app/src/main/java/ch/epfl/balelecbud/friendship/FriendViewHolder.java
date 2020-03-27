package ch.epfl.balelecbud.friendship;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class FriendViewHolder extends RecyclerView.ViewHolder {

        public TextView friendName;
        public Button deleteButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.text_view_friend_item_name);
            deleteButton = itemView.findViewById(R.id.buttonDeleteFriendItem);
        }
    }

