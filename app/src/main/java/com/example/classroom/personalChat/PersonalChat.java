package com.example.classroom.personalChat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalChat extends AppCompatActivity {
    EditText messege;
    Button btnSend;
    String chatIdStr;
    Long tsLong;
    LinearLayout linearLayout;
    String currentUserId, textMsg, friendId;
    DocumentReference personalChatId;
    NestedScrollView scrlView;
    RecyclerView rv;
    FirebaseFirestore db;
    String msg = null;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mPersonalAdapter;
    private RecyclerView.LayoutManager mPersonalLayoutManager;
    private DatabaseReference mDatabase;
    private Date dateTime;
    private ArrayList<PersonalObject> resultsPersonal = new ArrayList<PersonalObject>();

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);
        btnSend = findViewById(R.id.send);
        scrlView = findViewById(R.id.scrollView);
        rv = findViewById(R.id.recyclerView);
        messege = findViewById(R.id.message);

        friendId = getIntent().getExtras().getString("chatId");
        // friendId = "AhS3B153mOXhbukgYLV4wGqfggf2";
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        linearLayout = findViewById(R.id.sendLayout);
        Log.i("CHATID", friendId);

        final DocumentReference chatId = db.collection("USERS").document(currentUserId).collection("Friends").document("Lists");
        chatId.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                //chatIdData= task.getResult();
                //DocumentSnapshot ds = task.getResult();
                //ds.get(friendId);
                //Map m = ds.getData();
//                Log.i("task result",m.toString());
//
//                Log.i("task result",m.get(friendId).toString());
//
//                Log.i("task result",task.getResult().toString());
                DocumentSnapshot ds = task.getResult();
                Map<String, Object> map = ds.getData();
                //    for


                Log.i("MAPP", map.toString());


                String extract = task.getResult().toString();
                Log.i("my string", extract);

                Pattern p = Pattern.compile("PersonalChats/(.*?), ");
                Matcher ms = p.matcher(extract);
                String sub = null;
                while (ms.find()) {
                    sub = ms.group(1).substring(0, ms.group(1).length() - 1);

                    //is your string. do what you want
                    if ((sub.contains(")") || sub.contains("}"))) {
                        sub = sub.replaceAll("\\)", "");
                        sub = sub.replaceAll("\\}", "");
                    }
                }

                sub = friendId;


                if (sub == null) {
                    Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG", Toast.LENGTH_SHORT);
                } else
                    chatIdStr = sub;
                Log.i("may be str", sub);


                //String sub = extract.substring(extract.indexOf("dMap{("),extract.indexOf(")"));
                //String[] subStr = sub.split("Chats/");


                //chatIdStr = chatIdData.get("QZesm4wN2GFYkekzkqsn").toString();
                // FirebaseFirestore.getInstance().document(chatIdStr).get();

                //Log.i("data",chatIdData.get("QZesm4wN2GFYkekzkqsn").toString());
//                Log.i("dataChatId",FirebaseFirestore.getInstance().document(chatIdStr).get().toString());


                //personalChatId = db.collection("USERS").document(currentUserId).collection("Friends").document("List");

                //    personalChatId = db.collection("USERS").document("Chat").collection("PersonalChats").document(chatIdStr);
                //String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Chat").child(chatIdStr);
                //    Log.i("firebase",key);


                getChatMessage();

            }
        });


        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mPersonalLayoutManager = new LinearLayoutManager(PersonalChat.this);
        mRecyclerView.setLayoutManager(mPersonalLayoutManager);

        mPersonalAdapter = new PersonalAdapter(getDataSetPersonal(), PersonalChat.this);
        mRecyclerView.setAdapter(mPersonalAdapter);

//
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(PersonalChat.this,
//                DividerItemDecoration.VERTICAL));


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();


            }
        });
    }

    private void send() {
        String textMsg = messege.getText().toString();
        if (!textMsg.isEmpty()) {
//
//             tsLong= System.currentTimeMillis()/1000;
//            String ts = tsLong.toString();

            DatabaseReference newMessageDb = mDatabase.push();
            Map newMessage = new HashMap();
            newMessage.put("CreatedByUser", currentUserId);
            newMessage.put("text", textMsg);
            newMessageDb.setValue(newMessage);


//            Map<String, Object> chat = new HashMap<>();
//            chat.put(currentUserId + " | " + ts,textMsg);
//            personalChatId.set(chat, SetOptions.merge());
        }
        messege.setText(null);

    }

    public void getChatMessage() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {
                    //  String messege = null;
                    String createdBy = null;

                    if (dataSnapshot.child("text").getValue() != null) {
                        msg = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("CreatedByUser").getValue() != null) {
                        createdBy = dataSnapshot.child("CreatedByUser").getValue().toString();
                    }


                    if (msg != null && createdBy != null) {
                        Boolean currentuserBool = false;
                        if (createdBy.equals(currentUserId)) {
                            currentuserBool = true;
                        }

                        PersonalObject newMessage = new PersonalObject(msg, currentuserBool);
                        resultsPersonal.add(newMessage);
                        mPersonalAdapter.notifyDataSetChanged();

                    }
                    //    scrlView.fullScroll(View.FOCUS_DOWN);
                    scrlView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            scrlView.fullScroll(View.FOCUS_DOWN);

                        }
                    });


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
//        personalChatId.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if(documentSnapshot.exists()){
//
//
//                    msg =  documentSnapshot.getData().toString();
//                    Log.i("msg",msg);
//
//
////                    Timestamp stamp = new Timestamp(1549701580,0);
////                    Log.i("hello ",stamp.toDate().toString());
//
//
//
//
//                }
//
//                PersonalObject newMessage = new PersonalObject(msg,true);
//                resultsPersonal.add(newMessage);
//                mPersonalAdapter.notifyDataSetChanged();
//
//                if (documentSnapshot.getData()!= null)
//                Log.i("DATA REAL TIME",documentSnapshot.getData().toString());
//            }
//        });

    }

    private List<PersonalObject> getDataSetPersonal() {
        return resultsPersonal;
    }


}
