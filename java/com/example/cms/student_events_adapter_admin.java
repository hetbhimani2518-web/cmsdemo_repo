package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class student_events_adapter_admin extends RecyclerView.Adapter<student_events_adapter_admin.ViewHolder> {

    private ArrayList<student_events_model_admin> eventList;

    public student_events_adapter_admin(ArrayList<student_events_model_admin> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public student_events_adapter_admin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_event_admin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull student_events_adapter_admin.ViewHolder holder, int position) {
        student_events_model_admin event = eventList.get(position);
        holder.tvEventTitle.setText(event.getTitle());
        holder.tvEventDescription.setText(event.getDescription());
        holder.tvEventDate.setText(event.getDate());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDescription, tvEventDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
        }
    }
}
