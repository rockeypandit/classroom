package com.example.classroom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatHolder> {
    List<GroupChatObject> groupChats;
    private Context context;

    public GroupChatAdapter(List<GroupChatObject> groupChats, Context context) {
        this.groupChats = groupChats;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_chat_item, null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(lp);

        GroupChatHolder groupChatHolder = new GroupChatHolder(layout);
        return groupChatHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatHolder groupChatHolder, int i) {
        groupChatHolder.messageText.setText(groupChats.get(i).getMessage());
        groupChatHolder.senderNameText.setText(groupChats.get(i).getSenderName());

        if (groupChats.get(i).getCurrentUser()) {
            groupChatHolder.mContainer.setGravity(Gravity.END);
            //groupChatHolder.messageText.setTextColor(Color.parseColor("#404040"));
            groupChatHolder.messageText.setBackgroundResource(R.drawable.white_bg);
        } else {
            groupChatHolder.mContainer.setGravity(Gravity.START);
            //groupChatHolder.mContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //groupChatHolder.messageText.setTextColor(Color.parseColor("#FFFFFF"));
            groupChatHolder.messageText.setBackgroundResource(R.drawable.blue_bg);
        }
    }

    public void updateList(List<GroupChatObject> newList) {
        groupChats = newList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return groupChats.size();
    }
}
