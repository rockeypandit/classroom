package com.example.classroom;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MemberGrpAdapter extends RecyclerView.Adapter<MemberGrpAdapter.RVHolder> {
    private ArrayList<ChatObject> data;

    public static class RVHolder extends RecyclerView.ViewHolder {
        public TextView userNameText;

        public RVHolder(View v) {
            super(v);

            this.userNameText = itemView.findViewById(R.id.userNameText);
        }
    }

    public MemberGrpAdapter(ArrayList<ChatObject> groupMembers) {
        this.data = groupMembers;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grp_member_view, viewGroup, false);

        RVHolder vh = new RVHolder(view);
        return vh;
    }

    public void updateList(ArrayList<ChatObject> newList) {
        data = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final RVHolder holder, final int position) {
        TextView userNameText = holder.userNameText;
        userNameText.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}