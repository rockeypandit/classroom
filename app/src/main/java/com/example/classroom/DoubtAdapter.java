package com.example.classroom;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DoubtAdapter extends RecyclerView.Adapter<DoubtAdapter.DoubtViewHolder> {
    private ArrayList<DoubtModel> doubtSet;
    private FirebaseFirestore firestore;

    public DoubtAdapter(ArrayList<DoubtModel> doubts) {
        this.doubtSet = doubts;
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public DoubtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doubt_item, parent, false);

        view.setOnClickListener(new Doubts.OnDoubtItemClickListener(view.getContext()));
        view.setOnLongClickListener(new Doubts.OnDoubtItemLongClickListener(view.getContext()));
        DoubtViewHolder doubtViewHolder = new DoubtAdapter.DoubtViewHolder(view);
        return doubtViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoubtViewHolder holder, final int position) {
        TextView quesTextView = holder.questionTextView;
        TextView ansTextView = holder.answerTextView;

        quesTextView.setText(doubtSet.get(position).getQuestion());
        if (doubtSet.get(position).getAnswer() != null) {
            String ansStr = "Answer: " + doubtSet.get(position).getAnswer().substring(0, Math.min(doubtSet.get(position).getAnswer().length(), 10));
            ansTextView.setText(ansStr);
        } else {
            ansTextView.setText("Answer: Not Yet Answered.");
        }
    }

    @Override
    public int getItemCount() {
        return doubtSet.size();
    }

    public void updateList(ArrayList<DoubtModel> newList) {
        this.doubtSet = newList;
        notifyDataSetChanged();
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            this.questionTextView = itemView.findViewById(R.id.questionTextView);
            this.answerTextView = itemView.findViewById(R.id.answerTextView);
        }
    }
}
