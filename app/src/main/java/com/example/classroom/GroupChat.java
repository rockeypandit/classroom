package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class GroupChat extends AppCompatActivity {
    private EditText messageText;
    private Button sendButton, attachFileButton;
    private RecyclerView rv;
    private GroupChatAdapter groupAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;
    private FirebaseFirestore firestore;
    private List<GroupChatObject> messages;
    private Map<String, Object> groupChatData; //Snapshot of groupchat data with messages, members ids and group name
    private Map<String, String> userIdToNameMap; //Maps member uids to their username;
    private BiMap<String, String> memberToGrpMap; //Maps every group members UID to group chat ID
    private List<String> groupMemberIds; //User IDs of each group member
    private List<DocumentReference> groupDocRefs; //Docrefs for group chat entries which needs to be pushed when user sends a message
    private String groupName, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_chat);
        groupName = getIntent().getExtras().getString("groupName");
        setTitle(groupName);

        firestore = FirebaseFirestore.getInstance();
        messageText = findViewById(R.id.message);
        sendButton = findViewById(R.id.send);
        attachFileButton = findViewById(R.id.attach);
        rv = findViewById(R.id.recyclerView);
        currentUserId = FirebaseAuth.getInstance().getUid();

        messages = new ArrayList<>();
        userIdToNameMap = new HashMap<>();
        firestore.collection("USERS")
                .document("userNames")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        for (Map.Entry<String, Object> doc : documentSnapshot.getData().entrySet()) {
                            userIdToNameMap.put(doc.getKey(), doc.getValue().toString());
                        }
                    }
                });

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

                    //If groupchatdata has a messages field
                    if (groupChatData.containsKey("messages")) {
                        List<Map<String, String>> temp = (List<Map<String, String>>) groupChatData.get("messages");

                        if (temp != null) {
                            for (Map<String, String> t : temp) {
                                if (currentUserId.equals(t.get("senderId"))) {
                                    messages.add(new GroupChatObject(t.get("text"),
                                            userIdToNameMap.get(t.get("senderId")),
                                            true));
                                } else {
                                    messages.add(new GroupChatObject(t.get("text"),
                                            userIdToNameMap.get(t.get("senderId"))));
                                }
                            }
                            groupAdapter.updateList(messages);
                            rv.scrollToPosition(messages.size() - 1);
                        }
                    }

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


        rv.setNestedScrollingEnabled(false);
        rv.setHasFixedSize(false);

        groupLayoutManager = new LinearLayoutManager(GroupChat.this);
        ((LinearLayoutManager) groupLayoutManager).setSmoothScrollbarEnabled(true);
        ((LinearLayoutManager) groupLayoutManager).setStackFromEnd(true);
        rv.setLayoutManager(groupLayoutManager);

        groupAdapter = new GroupChatAdapter(messages, GroupChat.this);
        rv.setAdapter(groupAdapter);
        rv.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestore.collection("USERS")
                .document("userNames")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, String> temp = new HashMap<>();
                        for (Map.Entry<String, Object> doc : documentSnapshot.getData().entrySet()) {
                            temp.put(doc.getKey(), doc.getValue().toString());
                        }
                        userIdToNameMap = temp;
                    }
                });

        firestore.collection("USERS")
                .document(currentUserId)
                .collection("Groups")
                .whereEqualTo("name", groupName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentReference ref = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                List<Map<String, String>> tempMsg = (List<Map<String, String>>) documentSnapshot.get("messages");

                                if (tempMsg != null) {
                                    if (tempMsg.size() > messages.size()) {
                                        for (int i = messages.size() - 1; i < tempMsg.size(); i++) {
                                            messages.add(new GroupChatObject(tempMsg.get(i).get("text"),
                                                    userIdToNameMap.get(tempMsg.get(i).get("senderId"))
                                            ));
                                        }

                                        groupAdapter.notifyDataSetChanged();
                                        rv.scrollToPosition(messages.size() - 1);
                                    }
                                }
                            }
                        });
                    }
                });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, String> message = new HashMap<>();
                message.put("senderId", currentUserId);
                message.put("text", messageText.getText().toString());
                message.put("timestamp", new Date().toString());

                messages.add(new GroupChatObject(
                        userIdToNameMap.get(message.get("senderId")),
                        message.get("text"),
                        true
                ));

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
                rv.scrollToPosition(messages.size() - 1);
            }
        });
    }
}
