package com.example.classroom;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupChatHolder extends RecyclerView.ViewHolder {
    public TextView messageText;
    public TextView senderNameText;
    public LinearLayout mContainer;

    public GroupChatHolder(@NonNull View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.message);
        senderNameText = itemView.findViewById(R.id.sender_name);
        mContainer = itemView.findViewById(R.id.container);
    }
}
