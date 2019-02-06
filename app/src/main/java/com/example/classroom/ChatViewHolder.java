package com.example.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.classroom.personalChat.PersonalChat;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mChatId,mChatName;
    ImageView mChatImage;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mChatId = itemView.findViewById(R.id.Matchid);
        mChatName = itemView.findViewById(R.id.MatchName);
        mChatImage = itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), PersonalChat.class);
        Bundle b = new Bundle();
        b.putString("chatId",mChatId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }
}
