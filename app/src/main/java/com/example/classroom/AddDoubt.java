package com.example.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddDoubt extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText questionEditText;
    private EditText answerEditText;
    private EditText keywordsEditText;
    private Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_doubt);
        firestore = FirebaseFirestore.getInstance();
        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        keywordsEditText = findViewById(R.id.keywordsEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String ques = questionEditText.getText().toString();
                String ans = answerEditText.getText().toString();
                ArrayList<String> keywords = new ArrayList<>();
                keywords.addAll(Arrays.asList(keywordsEditText.getText().toString().split(",")));

                Map<String, Object> doubt = new HashMap<>();
                doubt.put("question", ques);
                doubt.put("keywords", keywords);
                doubt.put("answer", ans);

                if (ques.length() > 1) {
                    firestore.collection("doubts").document().set(doubt).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar succesSnackbar = Snackbar.make(v, "Successfully Added Doubt.", Snackbar.LENGTH_SHORT);
                            succesSnackbar.show();
                            Doubts.loadDoubtsfromDB();
                            startActivity(new Intent(v.getContext(), MainActivity.class));
                        }
                    });
                } else {
                    Snackbar failSnackbar = Snackbar.make(v, "Incomeplete data, please fill all fields properly", Snackbar.LENGTH_SHORT);
                    failSnackbar.show();
                }
            }
        });
    }
}
