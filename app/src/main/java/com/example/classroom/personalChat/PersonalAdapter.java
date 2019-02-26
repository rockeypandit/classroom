package com.example.classroom.personalChat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.classroom.R;

import java.util.List;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalViewHolder> {

    List<PersonalObject> chatLists;
    private Context context;


    public PersonalAdapter(List<PersonalObject> lists, Context context) {
        this.chatLists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //INFLATER
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        PersonalViewHolder cvh = new PersonalViewHolder(layoutView);

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalViewHolder chatViewHolder, int i) {
        chatViewHolder.mMessage.setText(chatLists.get(i).getMessage());
        if (chatLists.get(i).getCurrentUser()) {
            chatViewHolder.mContainer.setGravity(Gravity.END);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#404040"));
            chatViewHolder.mMessage.setBackgroundResource(R.drawable.white_bg);
            // chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        } else {
            // chatViewHolder.mMessage.setGravity(Gravity.START);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            chatViewHolder.mMessage.setBackgroundResource(R.drawable.blue_bg);
            chatViewHolder.mContainer.setGravity((Gravity.START));

            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {

        return chatLists.size();
    }
}
