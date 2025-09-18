package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class faculty_evets_adapter_admin extends RecyclerView.Adapter<faculty_evets_adapter_admin.ViewHolder>{

    private final ArrayList<faculty_events_model_admin> eventList;

    public faculty_evets_adapter_admin(ArrayList<faculty_events_model_admin> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public faculty_evets_adapter_admin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faculty_events_admin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull faculty_evets_adapter_admin.ViewHolder holder, int position) {
        faculty_events_model_admin event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.content.setText(event.getContent());
        holder.date.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            content = itemView.findViewById(R.id.eventContent);
            date = itemView.findViewById(R.id.eventDate);
        }
    }
}
