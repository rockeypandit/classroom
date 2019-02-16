package com.example.classroom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doubts extends Fragment {
    private static RecyclerView doubtRecyclerView;
    private static DoubtAdapter mAdapter;
    private static ArrayList<DoubtModel> data;
    private static FirebaseFirestore firestore;
    private static FirebaseUser currentUser;
    private static List<DocumentSnapshot> documentSnapshots;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton addDoubtFAB;
    private TextInputEditText searchEditText;

    @javax.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.doubt, container, false);
        searchEditText = view.findViewById(R.id.doubts_search_input);
        addDoubtFAB = view.findViewById(R.id.add_doubt_fab);
        addDoubtFAB.setOnClickListener(new fabClickListener(view.getContext()));
        doubtRecyclerView = view.findViewById(R.id.doubts_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        doubtRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        data = new ArrayList();

        firestore.collection("doubts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            documentSnapshots = queryDocumentSnapshots.getDocuments();

                            if (!documentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : documentSnapshots) {
                                    if (snapshot.get("answer") != null) {
                                        data.add(new DoubtModel(snapshot.get("question").toString(), snapshot.get("answer").toString()));
                                    } else {
                                        data.add(new DoubtModel(snapshot.get("question").toString()));
                                    }
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

        mAdapter = new DoubtAdapter(data);
        doubtRecyclerView.setAdapter(mAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<DoubtModel> filteredList = new ArrayList<>();

                for (DoubtModel d : data) {
                    if (d.getQuestion().contains(s.toString())) {
                        filteredList.add(d);
                    }
                }
                mAdapter.updateList(filteredList);
            }
        });
    }

    public static class OnDoubtItemClickListener implements View.OnClickListener {
        private final Context context;

        public OnDoubtItemClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(final View v) {
            int itemPos = doubtRecyclerView.getChildLayoutPosition(v);
            DoubtModel doubt = data.get(itemPos);
            Intent intent = new Intent(context, ShowDoubt.class);
            intent.putExtra("doubt", doubt);
            context.startActivity(intent);
        }
    }

    public static class OnDoubtItemLongClickListener implements View.OnLongClickListener {
        private final Context context;

        public OnDoubtItemLongClickListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onLongClick(final View v) {
            int itemPos = doubtRecyclerView.getChildLayoutPosition(v);

            final CollectionReference collection = firestore.collection("doubts");
            firestore.collection("doubts")
                    .whereEqualTo("question", data.get(itemPos).getQuestion())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    collection.document(documentSnapshot.getId()).delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Doubt Deleted.", Toast.LENGTH_SHORT);
                                                }
                                            });
                                }
                            }
                        }
                    });
            data.remove(itemPos);
            mAdapter.notifyItemRemoved(itemPos);
            mAdapter.notifyItemRangeChanged(itemPos, data.size());
            return true;
        }
    }

    public static class fabClickListener implements View.OnClickListener {
        final Context context;

        public fabClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(final View v) {
            firestore.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.get("position") != null) {
                        if (docSnap.get("position").toString() == "STUDENT") {
                            showAddQuestionDialog(v);
                        } else {
                            Intent intent = new Intent(context, AddDoubt.class);
                            v.getContext().startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(context, AddDoubt.class);
                        v.getContext().startActivity(intent);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Intent intent = new Intent(context, AddDoubt.class);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public static void showAddQuestionDialog(final View v) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        final EditText quesEditText = new EditText(v.getContext());
        dialogBuilder.setTitle("Add New Question");
        dialogBuilder.setView(quesEditText);

        dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quesiton = quesEditText.getText().toString();

                Map<String, String> doubtData = new HashMap<>();
                doubtData.put("question", quesiton);

                firestore.collection("doubts").add(doubtData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Snackbar succeSnackbar = Snackbar.make(v, "Successfully Added new Lecture.", Snackbar.LENGTH_SHORT);
                        succeSnackbar.show();
                    }
                });
                Doubts.loadDoubtsfromDB();
            }
        });

        dialogBuilder.create();
        dialogBuilder.show();
    }

    public static void loadDoubtsfromDB() {
        final ArrayList<DoubtModel> dataSet = new ArrayList<>();
        firestore.collection("doubts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            documentSnapshots = queryDocumentSnapshots.getDocuments();

                            if (!documentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : documentSnapshots) {
                                    if (snapshot.get("answer") != null) {
                                        if (snapshot.get("attachmentLink") == null) {
                                            dataSet.add(new DoubtModel(snapshot.get("question").toString(), snapshot.get("answer").toString()));
                                        } else {
                                            dataSet.add(new DoubtModel(
                                                    snapshot.get("question").toString(),
                                                    snapshot.get("answer").toString(),
                                                    snapshot.get("attachmentLink").toString()));
                                        }
                                    } else {
                                        dataSet.add(new DoubtModel(snapshot.get("question").toString()));
                                    }
                                }
                            }
                            mAdapter.updateList(dataSet);
                        }
                    });
                } else {
                    Log.d("DB ERROR", "Cannot load doubts.");
                }
            }
        });
    }
}
