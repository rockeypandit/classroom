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

    DocumentSnapshot document;
    String currentUserId;
    boolean flag;
    Map<String, Object> documentData;
    String temp;
    String[] arr1;
    String[] arr2 = new String[2];
    List<String> friendList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    //FIREBASE
    private DatabaseReference mDatabase;
    private ArrayList<ChatObject> resultsChats = new ArrayList<ChatObject>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);
        friendList = new ArrayList<>();


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //FIREBASE
        String key = FirebaseDatabase.getInstance().getReference().child("CHhat").push().getKey();
        Log.i("KEY", key);

        //mDatabase.child("User").child("Chat").setValue("data");


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put(currentUserId, "hi");
        // db.collection("USERS").add(data);
        DocumentReference chatId = db.collection("USERS").document("Chat").collection("PersonalChats").document();


        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mChatLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mChatLayoutManager);

        mChatAdapter = new ChatAdapter(getDatasetChat(), getContext());
        mRecyclerView.setAdapter(mChatAdapter);


        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        ChatObject obj = new ChatObject("userID", "NAME", "IMAGE");


        for (int i = 0; i < 100; i++)
            resultsChats.add(obj);


        mChatAdapter.notifyDataSetChanged();


        //CHATS


        //chatId.set(data);

        flag = true;

        Map<String, Object> storeChatId = new HashMap<>();


        final String friendUid = "AhS3B153mOXhbukgYLV4wGqfggf2";

        documentData = new HashMap<>();


        Log.i("CHATID1", chatId.toString());
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

//                            for (int j=0;j<arr1.length;j++){
//                                Log.i("VALUES arr",arr1[j]+",");
//                            }

                            arr1[0] = arr1[0].replaceAll("\\{", " ");
                            Log.i("Final usernames 0", arr1[0]);


                            arr1[1] = arr1[1].replaceAll("\\}", " ");


                            //  Log.i("Final usernames 0",arr1[0]);
                            //  Log.i("Final usernames 1",arr1[1]);

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
                            friendList.add(arr2[0]);
                        }


                    }


                    //  Log.i("VALUES",listArr[i]);


//                    for (Map.Entry<String, Object> entry : documentData.entrySet()) {
//
//                        Log.d("TAG123", entry.getValue().toString());
//
//
//                        if (entry.getKey().contains(currentUserId)) {
//
//                            Log.d("TAG12", entry.getValue().toString());
//
//                        }
//                    }
//

                    //Log.i("FRIENDS",friendList.get(0));

                    for (int i = 0; i < friendList.size(); i++) {
                        Log.i("FRIENDS", friendList.get(i));
                    }


                    if (document.exists()) {
                        Log.i("DATA RECEIVED", document.toString());
                        if (document.contains(currentUserId)) ;
                        flag = false;

                    }
                }
            }
        });

        //AFTER SEARCH ITEM CLICKED


        if (flag) {


            storeChatId.put(currentUserId + " | " + friendUid, key);
            //db.collection("USERS").document(currentUserId).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());
            db.collection("USERS").document(currentUserId).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());

            Log.i("CHATID2", chatId.toString());


            // CHAT FRIEND ID


            // db.collection("USERS").document(friendUid).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());
            db.collection("USERS").document(friendUid).collection("Friends").document("Lists").set(storeChatId, SetOptions.merge());

        }


        return view;
    }

    private List<ChatObject> getDatasetChat() {

        return resultsChats;
    }


}




