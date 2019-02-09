package com.example.classroom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class Lectures extends Fragment {
    private static RecyclerView lecturesRecyclerView;
    private static CustomAdapter mAdapter;
    private static ArrayList<DataModel> data;
    private static FirebaseFirestore firestore;
    private static List<DocumentSnapshot> documentSnapshots;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton addLectureFAB;
    private TextInputEditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.lectures, container, false);
        searchEditText = view.findViewById(R.id.lectures_search_input);
        addLectureFAB = view.findViewById(R.id.add_lecture_fab);
        addLectureFAB.setOnClickListener(new fabClickListener(view.getContext()));
        lecturesRecyclerView = view.findViewById(R.id.lectures_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        lecturesRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        data = new ArrayList();
        firestore.collection("lectures").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            documentSnapshots = queryDocumentSnapshots.getDocuments();

                            if (!documentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : documentSnapshots) {
                                    data.add(new DataModel(snapshot.get("videoTitle").toString(), snapshot.get("videoUrl").toString()));
                                }
                            }
                            mAdapter.updateList(data);
                        }
                    });
                } else {
                    Snackbar succeSnackbar = Snackbar.make(getView(), "Unable to get data from Server.", Snackbar.LENGTH_SHORT);
                    succeSnackbar.show();
                }
            }
        });

        mAdapter = new CustomAdapter(data);
        lecturesRecyclerView.setAdapter(mAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<DataModel> filteredList = new ArrayList<>();

                for (DataModel d : data) {
                    if (d.getVideoTitle().contains(s.toString())) {
                        filteredList.add(d);
                    }
                }

                mAdapter.updateList(filteredList);
            }
        });
    }

    public static class OnCardClickListener implements View.OnClickListener {
        private final Context context;

        public OnCardClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            String vidUrl = v.getTag().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vidUrl));
            context.startActivity(intent);
        }
    }

    public static class fabClickListener implements View.OnClickListener {
        final Context context;

        public fabClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(final View v) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
            LinearLayout dialogLayout = new LinearLayout(v.getContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(30, 16, 30, 16);

            final EditText vidNameText = new EditText(v.getContext());
            vidNameText.setHint("Video Title");
            vidNameText.setId(R.id.alert_vid_name_text);
            dialogLayout.addView(vidNameText);

            final EditText vidUrlText = new EditText(v.getContext());
            vidUrlText.setHint("Video Share Link");
            vidUrlText.setId(R.id.alert_vid_url_text);
            dialogLayout.addView(vidUrlText);

            dialogBuilder.setView(dialogLayout);
            dialogBuilder.setTitle("Add New Lecture");

            dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String videoUrl = vidUrlText.getText().toString().trim();
                    String videoTitle = vidNameText.getText().toString().trim();

                    Map<String, String> videoData = new HashMap<>();
                    videoData.put("videoTitle", videoTitle);
                    videoData.put("videoUrl", videoUrl);

                    data.add(new DataModel(videoTitle, videoUrl));
                    mAdapter.updateList(data);

                    firestore.collection("lectures").add(videoData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Snackbar succeSnackbar = Snackbar.make(v, "Successfully Added new Lecture.", Snackbar.LENGTH_SHORT);
                            succeSnackbar.show();
                        }
                    });

                    /*firestore.collection("lectures").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        documentSnapshots = queryDocumentSnapshots.getDocuments();
                                        if (!documentSnapshots.isEmpty()) {
                                            for (DocumentSnapshot snapshot : documentSnapshots) {
                                                data.add(new DataModel(snapshot.get("videoTitle").toString(), snapshot.get("videoUrl").toString()));
                                        }
                                    }
                                });
                            } else {
                                Log.d("DATA", "Cannot Get Lectures from collection.");
                            }
                        }
                    });*/
                }
            });

            dialogBuilder.create();
            dialogBuilder.show();
        }
    }
}
