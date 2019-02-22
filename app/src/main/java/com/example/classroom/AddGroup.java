package com.example.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddGroup extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private AutoCompleteTextView searchUserText;
    private EditText grpNameText;
    private Button addUserBtn;
    private Button createGrpBtn;
    private RecyclerView grpMembersRV;
    private MemberGrpAdapter viewAdapter;
    private RecyclerView.LayoutManager viewManager;
    private Map<String, Object> listOfUserNames;
    private Map<String, String> userNamesToIdMap;
    private ArrayList<ChatObject> groupMembers;
    private String currentUid;

    @Override
    protected void onStart() {
        super.onStart();

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameToAdd = searchUserText.getText().toString();

                if (userNameToAdd.length() > 1) {
                    if (listOfUserNames.values().contains(userNameToAdd)) {
                        Snackbar.make(v, "Username is correct, Adding...", Snackbar.LENGTH_SHORT).show();

                        if (!groupMembers.contains(userNameToAdd)) {
                            groupMembers.add(new ChatObject(userNamesToIdMap.get(userNameToAdd), userNameToAdd, null));
                            viewAdapter.updateList(groupMembers);
                            searchUserText.setText("");
                        }
                    } else {
                        Snackbar.make(v, "Username not found.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "Invalid Username!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        createGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (groupMembers.size() < 2) {
                    Snackbar.make(v, "Group must have atleast 2 Members.", Snackbar.LENGTH_SHORT).show();
                } else if (grpNameText.getText().length() < 4) {
                    Snackbar.make(v, "Group name must atleast be 4 characters", Snackbar.LENGTH_SHORT).show();
                } else {
                    WriteBatch writeBatch = firestore.batch();

                    for (ChatObject member : groupMembers) {
                        String memberId = userNamesToIdMap.get(member.getName());
                        ArrayList<String> membersToAdd = new ArrayList<>();
                        for (ChatObject m : groupMembers) {
                            membersToAdd.add(userNamesToIdMap.get(m.getName()));
                        }

                        Map<String, Object> groupObject = new HashMap<>();
                        groupObject.put("name", grpNameText.getText().toString());
                        groupObject.put("members", membersToAdd);

                        CollectionReference clRef = firestore.collection("USERS").document(memberId).collection("Groups");
                        writeBatch.set(clRef.document(), groupObject);
                    }

                    writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(v, "Successfully created Group: " + grpNameText.getText().toString(), Snackbar.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.firestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.add_group);
        this.searchUserText = findViewById(R.id.userSearchText);
        this.grpNameText = findViewById(R.id.grpNameText);
        this.addUserBtn = findViewById(R.id.addUserBtn);
        this.createGrpBtn = findViewById(R.id.createGrpBtn);
        this.grpMembersRV = findViewById(R.id.grpUserRV);
        grpMembersRV.setHasFixedSize(true);
        viewManager = new LinearLayoutManager(this);
        grpMembersRV.setLayoutManager(viewManager);
        groupMembers = new ArrayList<>();
        viewAdapter = new MemberGrpAdapter(groupMembers);
        grpMembersRV.setAdapter(viewAdapter);
        this.currentUid = FirebaseAuth.getInstance().getUid();

        firestore.collection("USERS").document("userNames").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Snackbar.make(findViewById(android.R.id.content), "Couldn't Load Users.", Snackbar.LENGTH_SHORT).show();
                } else {
                    DocumentSnapshot snap = task.getResult();
                    listOfUserNames = snap.getData();
                    userNamesToIdMap = new HashMap<>();

                    for (Map.Entry<String, Object> user : listOfUserNames.entrySet()) {
                        userNamesToIdMap.put(user.getValue().toString(), user.getKey());
                    }

                    //String[] users = userNamesToIdMap.keySet().toArray(new String[0]);
                    //ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.id.userSearchText, users);
                    //searchUserText.setAdapter(listAdapter);
                }
            }
        });
    }
}
