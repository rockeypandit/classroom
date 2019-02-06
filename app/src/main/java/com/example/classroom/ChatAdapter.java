package com.example.classroom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    List<ChatObject> lists;
    private Context context;



    public  ChatAdapter(List<ChatObject> lists, Context context ){
        this.lists = lists;
        this.context = context;
    }



    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chats,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);






        ChatViewHolder cvh = new ChatViewHolder(layoutView);

         return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {


        //INFO TO SHOW IN CHAT PAGE


        chatViewHolder.mChatId.setText(lists.get(i).getUserId());
    }

    @Override
    public int getItemCount() {

        return lists.size();
    }
}
