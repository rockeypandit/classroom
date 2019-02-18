package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chats extends Fragment {
    FirebaseFirestore db;
    DocumentSnapshot document;
    DocumentSnapshot myFriendList;
    AutoCompleteTextView searchbar;
    Map<String, Object> storeChatId;
    String currentUserId;
    boolean flag, flagInternet = false;
    String[] totalUnames;
    Map<String, Object> documentData, mapFriendList;
    String temp, key;
    String[] arr1;
    String[] arr2 = new String[2];
    String friendUid;//= "AhS3B153mOXhbukgYLV4wGqfggf2";
    DocumentReference chatId;
    List<String> friendList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    List<String> fl = new ArrayList<>();

    //FIREBASE
    private DatabaseReference mDatabase;
    private ArrayList<ChatObject> resultsChats = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.chat, container, false);
        friendList = new ArrayList<>();

        searchbar = view.findViewById(R.id.search_bar);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendUid = currentUserId;
        //FIREBASE
        key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
        Log.i("KEY", key);

        //mDatabase.child("User").child("Chat").setValue("data");
        db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put(currentUserId, "hi");
        // db.collection("USERS").add(data);
        chatId = db.collection("USERS").document("Chat").collection("PersonalChats").document();
        final DocumentReference docFriendList = db.collection("USERS").document("userNames");
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mChatLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mChatLayoutManager);

        mChatAdapter = new ChatAdapter(getDatasetChat(), getContext());
        mRecyclerView.setAdapter(mChatAdapter);


        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        mChatAdapter.notifyDataSetChanged();

        //CHATS
        //chatId.set(data);

        flag = true;

        storeChatId = new HashMap<>();
        documentData = new HashMap<>();

        db.collection("USERS").document(currentUserId).collection("Friends").document("Lists").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    documentData = document.getData();
                    Log.i("DOCUMENT DATA", documentData.toString());
                    String[] listArr = documentData.toString().split(",");

                    for (int i = 0; i < listArr.length; i++) {

                        if (listArr[i].contains(currentUserId)) {
                            arr1 = listArr[i].split("=");

                            arr1 = arr1[0].split(" \\| ");

                            arr1[0] = arr1[0].replaceAll("\\{", " ");
                            //  Log.i("Final usernames 0", arr1[0]);

                            arr1[1] = arr1[1].replaceAll("\\}", " ");
                            arr1[0] = arr1[0].trim();
                            arr1[1] = arr1[1].trim();

                            if (arr1[0].equals(currentUserId)) {
                                arr2[0] = arr1[1];
                            } else
                                arr2[0] = arr1[0];

                            //  Log.i("Final usernames",arr2[0]);
                        }
                        if (arr2[0] != null && temp != arr2[0]) {
                            temp = arr2[0];
                            if (!(arr2[0].equals(currentUserId))) {
                                friendList.add(arr2[0]);
                            }
                        }
                    }

                    final Map<String, Object> mapFriend = new HashMap<>();
                    for (int i = 0; i < friendList.size(); i++) {

                        //resultsChats.add(myFriendList.get(friendList.get(i)).toString());
                        Log.i("FRIENDS", friendList.get(i));
//                        Log.i("FRIENDS", myFriendList.toString());

                        // mapFriend = myFriendList.getData();
                        // Log.i("FRIENDS",myFriendList.get("ajzCuGB2fobxxGLsvJuvRUK8YQ92").toString());


                        //   Log.i("FRIENDS", myFriendList.get(friendList.get(i)).toString());
                    }

                    DocumentReference docRef = db.collection("USERS").document("userNames");
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document1 = task.getResult();
                                myFriendList = document1;

                                if (document1.exists()) {
                                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                                    view.findViewById(R.id.relativeChat).setVisibility(View.VISIBLE);
                                    Log.d("my DATA", "DocumentSnapshot data: " + document1.getData());
                                    flag = true;

                                    view.findViewById(R.id.progress).setVisibility(View.GONE);
                                } else {
                                    Log.d("ERROR", "No such document");
                                }

                                Log.i("total usernames", myFriendList.toString());
                                final Map<String, Object> mapTotal = myFriendList.getData();

                                final List<String> keyList = new ArrayList<>(mapTotal.keySet());

                                Log.i("KEYS", keyList.size() + "");
                                totalUnames = mapTotal.values().toArray(new String[0]);

//                                for (int i =0;i<l.size();i++)
//                                    Log.i(l.get(i),totalUnames[i]);


                                // BiMap<String, Object> biMap = HashBiMap.create();


                                totalUnames = mapTotal.values().toArray(new String[0]);
                                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, totalUnames);
                                searchbar.setAdapter(adapter);


                                for (int i = 0; i < friendList.size(); i++) {
                                    temp = friendList.get(i);
                                    Log.i("ERROR12", temp);
                                    fl.add(myFriendList.get(friendList.get(i)).toString());
                                    //   Log.i("ERROR12",fl.get(i));


                                    ChatObject obj = new ChatObject(temp, fl.get(i), "IMAGE");

                                    resultsChats.add(obj);
                                    // fl.add(document.get(temp).toString());
                                    Log.i("fl", fl.get(i));

                                }


                                searchbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Log.i("CLICKED", searchbar.getText().toString());
                                        int index = -1;
                                        for (int i = 0; i < totalUnames.length; i++) {
                                            if (totalUnames[i].equals(searchbar.getText().toString())) {
                                                index = i;
                                                break;
                                            }
                                        }


                                        Log.i(searchbar.getText().toString(), keyList.get(index));


                                        if (document.exists()) {
                                            Log.i("DATA RECEIVED", document.toString());
                                            if (document.contains(currentUserId)) ;
                                            flag = false;

                                        }


                                        friendUid = keyList.get(index);
                                        flag = true;
                                    }
                                });


                            } else {
                                Log.d("ERROR", "get failed with ", task.getException());
                            }
                        }
                    });

                    // resultsChats.add()
                    if (documentData.containsKey(friendUid))
                        flag = false;


                    view.findViewById(R.id.btnSelect).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("RUNNIONNG", "true");

                            if (flag) {

                                Log.i("RUNNIONNG", "true");

                                storeChatId.put(currentUserId + " | " + friendUid, key);
                                //db.collection("USERS").document(currentUserId).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());
                                db.collection("USERS").document(currentUserId).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());

                                Log.i("CHATID2", chatId.toString());
                                // CHAT FRIEND ID
                                // db.collection("USERS").document(friendUid).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());
                                db.collection("USERS").document(friendUid).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());

                            }
                        }
                    });
                }
            }
        });
        //AFTER SEARCH ITEM CLICKE
        //SEARCH BAR
        return view;
    }

    private List<ChatObject> getDatasetChat() {
        return resultsChats;
    }
}