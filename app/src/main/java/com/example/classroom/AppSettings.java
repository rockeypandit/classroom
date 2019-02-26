package com.example.classroom;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AppSettings extends AppCompatActivity {
    private EditText userNameEditText, organisationEditText, positionEditText;
    private ImageView profilePicView;
    private Button updateInfoButton;
    private TextView userNameText, userIdText;
    private FirebaseFirestore firestore;
    private FirebaseStorage storageReference;
    String currentUserId;
    Uri userPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageReference = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userNameEditText = findViewById(R.id.nameEditText);
        organisationEditText = findViewById(R.id.organisationEditText);
        positionEditText = findViewById(R.id.positionEditText);
        profilePicView = findViewById(R.id.profilePicIV);
        updateInfoButton = findViewById(R.id.updateInfoButton);
        userNameText = findViewById(R.id.userNameText);
        userIdText = findViewById(R.id.uidText);

        currentUserId = FirebaseAuth.getInstance().getUid();
        userPhoto = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

        firestore.collection("USERS")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> user = documentSnapshot.getData();

                        userIdText.setText(currentUserId);
                        userNameText.setText(user.get("username").toString());
                        userNameEditText.setText(user.get("username").toString());
                        organisationEditText.setText(user.get("School").toString());
                        if (user.get("position") != null) {
                            positionEditText.setText(user.get("position").toString());
                        }
                        positionEditText.setEnabled(false);

                        if (user.get("customPhoto") != null) {
                            Picasso.get().load(user.get("customPhoto").toString()).into(profilePicView);
                        } else {
                            Picasso.get().load(userPhoto).into(profilePicView);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        profilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] mimeTypes = {"image/*"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image for Profile"), 42);
            }
        });

        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String newUserName = userNameEditText.getText().toString();
                String newOrg = organisationEditText.getText().toString();

                Map<String, Object> newInfo = new HashMap<>();
                newInfo.put("username", newUserName);
                newInfo.put("School", newOrg);

                if (newUserName.length() > 4 && newOrg.length() > 2) {
                    firestore.collection("USERS")
                            .document(currentUserId)
                            .update(newInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(v, "Successfully Updated Information.", Snackbar.LENGTH_SHORT).show();
                                    startActivity(new Intent(v.getContext(), MainActivity.class));
                                }
                            });
                } else {
                    Snackbar.make(v, "Invalid Input. Retry.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 42 && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Snackbar.make(findViewById(android.R.id.content), "Starting Upload Process..", Snackbar.LENGTH_SHORT).show();

                ContentResolver cr = getApplicationContext().getContentResolver();
                String fileType = cr.getType(uri);
                String fileName = uri.getPath();
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType(fileType)
                        .build();

                final StorageReference ref = storageReference.getReference().child(fileName);
                UploadTask uploadTask = ref.putFile(uri, metadata);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri taskResult = task.getResult();
                            Map<String, Object> newPic = new HashMap<>();
                            newPic.put("customPhoto", taskResult.toString());
                            Picasso.get().load(taskResult).into(profilePicView);

                            firestore.collection("USERS")
                                    .document(currentUserId)
                                    .update(newPic)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(findViewById(android.R.id.content), "Successfully updated Profile picture.", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Error during uploading.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}
