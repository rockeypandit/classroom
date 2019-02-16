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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddDoubt extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText questionEditText;
    private EditText answerEditText;
    private EditText keywordsEditText;
    private Button attachButton;
    private Button submitButton;
    private FirebaseStorage storageReference;

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
                            Map<String, String> doubtMap = new HashMap<>();
                            doubtMap.put("question", questionEditText.getText().toString());
                            doubtMap.put("answer", answerEditText.getText().toString());
                            doubtMap.put("attachmentLink", taskResult.toString());

                            Log.d("ATTACHMENT", doubtMap.toString());
                            firestore.collection("doubts").document().set(doubtMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar successSnack = Snackbar.make(findViewById(android.R.id.content), "Successfully Added Dobut.", Snackbar.LENGTH_SHORT);
                                    successSnack.show();
                                    Doubts.loadDoubtsfromDB();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            });
                        } else {
                            Snackbar successSnack = Snackbar.make(findViewById(android.R.id.content), "Successfully Added Dobut.", Snackbar.LENGTH_SHORT);
                            successSnack.show();
                            Doubts.loadDoubtsfromDB();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_doubt);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance();
        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        keywordsEditText = findViewById(R.id.keywordsEditText);
        attachButton = findViewById(R.id.attachFiles);
        submitButton = findViewById(R.id.submitButton);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain", "image/*",
                                "application/pdf",
                                "application/zip"};

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
                startActivityForResult(Intent.createChooser(intent, "Choose Files to Add"), 42);
            }
        });

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
