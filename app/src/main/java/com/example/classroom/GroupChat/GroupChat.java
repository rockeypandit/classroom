package com.example.classroom.GroupChat;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class GroupChat extends AppCompatActivity {
    private EditText messageText;
    private Button sendButton;
    private LinearLayout linearLayout;
    private NestedScrollView scrollView;
    private RecyclerView rv;
    private RecyclerView.Adapter groupAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;
    private FirebaseFirestore firestore;
    private ArrayList<GroupMessage> messages;
    private String groupName;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_chat);
        messageText = findViewById(R.id.message);
        rv = findViewById(R.id.recyclerView);

        currentUserId = FirebaseAuth.getInstance().getUid();
        groupName = getIntent().getExtras().getString("groupName");

        firestore.collection("USERS").document(currentUserId)
                .collection("Groups")
                .whereEqualTo("name", groupName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    e.printStackTrace();
                }

                List<DocumentSnapshot> snaps = queryDocumentSnapshots.getDocuments();

                for (DocumentSnapshot s : snaps) {
                    Log.d("GROUPCHAT DATA", s.getData().toString());
                }
            }
        });
    }
}
