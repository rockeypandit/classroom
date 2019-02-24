package com.example.classroom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    FirebaseFirestore db;
    EditText uName, edtSchool;
    String userName, exist = "temp", std;
    TextView userAvail;
    boolean canexist;
    Button c1Next, finish;
    CardView c1, c2;
    String school;
    Spinner user;
    DocumentReference mDocRefInfo, mDocRefUname;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        uName = findViewById(R.id.uname);
        userAvail = findViewById(R.id.userAvailable);
        userName = uName.getText().toString();
        c1 = findViewById(R.id.c1);
        edtSchool = findViewById(R.id.school);
        finish = findViewById(R.id.finish);
        user = findViewById(R.id.user);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //  Log.i("dATABASE",mDocRef.toString());
        mDocRefInfo = FirebaseFirestore.getInstance().collection("USERS").document("userInfo");
        mDocRefUname = FirebaseFirestore.getInstance().collection("USERS").document("userNames");


        List<String> classes = new ArrayList<String>();
        classes.add("STUDENT");
        classes.add("MASTER");


        ArrayAdapter<String> dataAdapterCategories = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classes);
        dataAdapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user.setAdapter(dataAdapterCategories);


        uName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userName = uName.getText().toString();
                if (s.length() > 3) {

                    mDocRefUname.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                // Log.i("DOCCC",document.getData().get().toString());
                                // document.get("userNames");
                                Map<String, Object> fetchData = document.getData();
                                String buff = fetchData.toString();
                                Log.i("datadd", fetchData.toString());

                                //IF NOT WORKING PROPERLY

                                try {
                                    Log.i("userName", userName);

                                    if (buff.contains(userName)) {

                                        Log.i("try11", document.getString(userName));

                                        userAvail.setText("@" + userName + "  Not Available");
                                        userAvail.setTextColor(Color.RED);
                                        canexist = false;
                                    } else {
                                        userAvail.setText("@" + userName + " Available");
                                        userAvail.setTextColor(Color.GREEN);
                                        canexist = true;


                                    }
                                } catch (Exception e) {
                                    userAvail.setText("");
                                }


                            } else {
                                //Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                } else
                    userAvail.setText("");


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                school = edtSchool.getText().toString();
                std = user.getSelectedItem().toString();
                if (school.length() < 4)
                    edtSchool.setError("invalid");
                else {


                    Map<String, Object> user = new HashMap<>();
                    user.put("username", userName);
                    user.put("position", std);
                    user.put("School", school);

                    Map<String, Object> userNameMap = new HashMap<>();
                    userNameMap.put(currentUser.getUid(), userName);


                    //  db.collection("USERS").add(crrNameMap);
                    db.collection("USERS").document(currentUser.getUid()).set(user, SetOptions.merge());


//                    mDocRefInfo.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void Void) {
//
//
//                          //  Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_LONG);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                         //   Toast.makeText(getApplicationContext(),"FAILED",Toast.LENGTH_LONG);
//
//                        }
//                    });
//

                    mDocRefUname.set(userNameMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                            //  Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_LONG);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //   Toast.makeText(getApplicationContext(),"FAILED",Toast.LENGTH_LONG);

                        }
                    });
                }
            }
        });
    }
}
