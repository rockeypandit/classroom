package com.example.classroom.personalChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.classroom.R;

import java.util.List;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalViewHolder>{

    List<PersonalObject> chatLists;
    private Context context;



    public PersonalAdapter(List<PersonalObject> lists, Context context ){
        this.chatLists = lists;
        this.context = context;
    }



    @NonNull
    @Override
    public PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //INFLATER
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chats,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);






        PersonalViewHolder cvh = new PersonalViewHolder(layoutView);

         return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalViewHolder chatViewHolder, int i) {
    }

    @Override
    public int getItemCount() {

        return chatLists.size();
    }
}
