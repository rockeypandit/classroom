package com.example.classroom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<DataModel> dataModels;

    public CustomAdapter(ArrayList<DataModel> data) {
        this.dataModels = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        view.setOnClickListener(new Lectures.OnCardClickListener(view.getContext()));
        view.setOnLongClickListener(new Lectures.OnCardLongClickListener(view.getContext()));
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView titleTextVIew = holder.titleTextView;
        ImageView thumbnailImageView = holder.thumbnailImageView;

        titleTextVIew.setText(dataModels.get(listPosition).getVideoTitle());
        Picasso.get()
                .load(dataModels.get(listPosition).getThumbnailUrl())
                .placeholder(R.drawable.ytb_placeholder)
                .error(R.drawable.ytb_placeholder)
                .into(thumbnailImageView);

        String videoUrl = dataModels.get(listPosition).getVideoUrl();
        holder.itemView.setTag(videoUrl);
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public void updateList(ArrayList<DataModel> newList) {
        dataModels = newList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView thumbnailImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.card_view_image_title);
            this.thumbnailImageView = itemView.findViewById(R.id.card_view_image);
        }
    }
}
