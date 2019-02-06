package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Chats extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    String currentUserId;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat,container,false);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mChatLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mChatLayoutManager);

        mChatAdapter = new ChatAdapter(getDatasetChat(),getContext());
        mRecyclerView.setAdapter(mChatAdapter);



        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        ChatObject obj = new ChatObject("asd","fgd","dfssd");
        for(int i = 0 ;i<100;i++)
        resultsChats.add(obj);
        resultsChats.add(obj);
        resultsChats.add(obj);
        resultsChats.add(obj);
        resultsChats.add(obj);
        resultsChats.add(obj);resultsChats.add(obj);
        resultsChats.add(obj);

        mChatAdapter.notifyDataSetChanged();








        return view;
    }
private ArrayList<ChatObject> resultsChats= new ArrayList<ChatObject>();
    private List<ChatObject> getDatasetChat() {
        return resultsChats;
    }


}




