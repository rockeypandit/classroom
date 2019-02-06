package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class Lectures extends Fragment {
    private static RecyclerView lecturesRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<DataModel> data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lectures, container, false);
        lecturesRecyclerView = view.findViewById(R.id.lectures_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        lecturesRecyclerView.setLayoutManager(layoutManager);

        data = new ArrayList();

        for (int i = 0; i < TestData.titleArr.length; i++) {
            data.add(new DataModel(TestData.videoIdArr[i], TestData.titleArr[i]));
        }

        mAdapter = new CustomAdapter(data);
        lecturesRecyclerView.setAdapter(mAdapter);

        return view;
    }
}
