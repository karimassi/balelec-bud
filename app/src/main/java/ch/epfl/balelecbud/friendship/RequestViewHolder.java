package ch.epfl.balelecbud.friendship;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.balelecbud.R;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public TextView friendName;
    public Button acceptButton;
    public Button deleteButton;

    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
        friendName = itemView.findViewById(R.id.text_view_friend_request_item_name);
        acceptButton = itemView.findViewById(R.id.button_request_item_accept_request);
        deleteButton = itemView.findViewById(R.id.button_request_item_delete_request);
    }
}