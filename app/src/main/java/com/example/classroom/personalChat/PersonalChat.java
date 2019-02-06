package com.example.classroom.personalChat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.classroom.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PersonalChat extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mPersonalAdapter;
    private RecyclerView.LayoutManager mPersonalLayoutManager;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mPersonalLayoutManager = new LinearLayoutManager(PersonalChat.this);
        mRecyclerView.setLayoutManager(mPersonalLayoutManager);

        mPersonalAdapter = new PersonalAdapter(getDataSetPersonal(),PersonalChat.this);
        mRecyclerView.setAdapter(mPersonalAdapter   );



        mRecyclerView.addItemDecoration(new DividerItemDecoration(PersonalChat.this,
                DividerItemDecoration.VERTICAL));


    }

    private ArrayList<PersonalObject> resultsPersonal= new ArrayList<PersonalObject>();
    private List<PersonalObject> getDataSetPersonal() {
        return resultsPersonal;
    }

}
