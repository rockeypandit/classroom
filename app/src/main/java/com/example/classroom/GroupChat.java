package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChat extends AppCompatActivity {
    private EditText messageText;
    private Button sendButton, attachFileButton;
    private LinearLayout linearLayout;
    private NestedScrollView scrollView;
    private RecyclerView rv;
    private RecyclerView.Adapter groupAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;
    private FirebaseFirestore firestore;
    private ArrayList<GroupMessage> messages;
    private Map<String, Object> groupChatData;
    private BiMap<String, String> memberToGrpMap;
    private List<String> groupMemberIds;
    private List<DocumentReference> groupDocRefs;
    private String groupName, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_chat);
        firestore = FirebaseFirestore.getInstance();
        messageText = findViewById(R.id.message);
        sendButton = findViewById(R.id.send);
        attachFileButton = findViewById(R.id.attach);
        rv = findViewById(R.id.recyclerView);
        currentUserId = FirebaseAuth.getInstance().getUid();
        groupName = getIntent().getExtras().getString("groupName");

        firestore.collection("USERS").document(currentUserId)
                .collection("Groups")
                .whereEqualTo("name", groupName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("GROUPCHAT ERROR", task.getException().toString());
                    groupChatData = null;
                } else {
                    QuerySnapshot snap = task.getResult();
                    groupChatData = snap.getDocuments().get(0).getData();
                    groupMemberIds = (List<String>) groupChatData.get("members");

                    memberToGrpMap = HashBiMap.create();
                    groupDocRefs = new ArrayList<>();

                    for (final String member : groupMemberIds) {
                        firestore.collection("USERS")
                                .document(member)
                                .collection("Groups")
                                .whereEqualTo("name", groupName)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String grpId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                        memberToGrpMap.put(member, grpId);

                                        DocumentReference ref = firestore.collection("USERS")
                                                .document(member)
                                                .collection("Groups")
                                                .document(grpId);

                                        groupDocRefs.add(ref);
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, String> message = new HashMap<>();
                message.put("senderId", currentUserId);
                message.put("text", messageText.getText().toString());
                message.put("timestamp", new Date().toString());

                WriteBatch writeBatch = firestore.batch();

                for (DocumentReference ref : groupDocRefs) {
                    writeBatch.update(ref, "messages", FieldValue.arrayUnion(message));
                }

                writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        messageText.setText("");
                    }
                });
            }
        });
    }

    public List<GroupChatObject> getGroupMessagesFromDB() {
        final List<GroupChatObject> groupMessages = new ArrayList<>();

        firestore.collection("USERS")
                .document(currentUserId)
                .collection("Groups")
                .whereEqualTo("name", groupName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, Object> chatData = queryDocumentSnapshots.getDocuments().get(0).getData();

                        List<Map<String, String>> temp = (List<Map<String, String>>) chatData.get("messages");
                        for (Map<String, String> t : temp) {
                            groupMessages.add(new GroupChatObject(
                                    t.get("text"),
                                    t.get("senderId")
                            ));
                        }
                    }
                });
        Log.d("GROUP MESSAGES", groupMessages.toString());
        return groupMessages;
    }
}
