package com.example.classroom.personalChat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PersonalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public PersonalViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


    }
}
