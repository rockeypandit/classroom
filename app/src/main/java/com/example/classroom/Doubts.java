package com.example.classroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Doubts extends Fragment {
    private static RecyclerView doubtRecyclerView;
    private static DoubtAdapter mAdapter;
    private static ArrayList<DoubtModel> data;
    private static FirebaseFirestore firestore;
    private static List<DocumentSnapshot> documentSnapshots;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton addDoubtFAB;
    private TextInputEditText searchEditText;

    @javax.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                                    data.add(new DoubtModel(snapshot.get("question").toString(), snapshot.get("answer").toString()));
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
        public void onClick(View v) {
            Snackbar doubtSnackbar = Snackbar.make(v, "Item Pressed.", Snackbar.LENGTH_SHORT);
            doubtSnackbar.show();
        }
    }

    public static class fabClickListener implements View.OnClickListener {
        final Context context;

        public fabClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(final View v) {
            //TODO: Add here code for showing add_doubt activity.
            Intent intent = new Intent(context, AddDoubt.class);
            v.getContext().startActivity(intent);
        }
    }
}
