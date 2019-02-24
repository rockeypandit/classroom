package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AppSettings extends AppCompatActivity {
    private EditText userNameEditText, organisationEditText, positionEditText;
    private ImageView profilePicView;
    private Button updateInfoButton;
    private TextView userNameText, userIdText;
    private FirebaseFirestore firestore;
    String currentUserId, currentUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firestore = FirebaseFirestore.getInstance();
        userNameEditText = findViewById(R.id.nameEditText);
        organisationEditText = findViewById(R.id.organisationEditText);
        positionEditText = findViewById(R.id.positionEditText);
        profilePicView = findViewById(R.id.profilePicIV);
        updateInfoButton = findViewById(R.id.updateInfoButton);
        userNameText = findViewById(R.id.userNameText);
        userIdText = findViewById(R.id.uidText);

        currentUserId = FirebaseAuth.getInstance().getUid();

        firestore.collection("USERS")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> user = documentSnapshot.getData();

                        userNameText.setText(user.get("username").toString());
                        userNameEditText.setText(user.get("username").toString());
                        organisationEditText.setText(user.get("School").toString());
                        if (user.get("position") != null) {
                            positionEditText.setText(user.get("position").toString());
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userIdText.setText(currentUserId);
    }
}
