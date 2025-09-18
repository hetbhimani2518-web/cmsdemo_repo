package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertChatAdapterFaculty extends RecyclerView.Adapter<AlertChatAdapterFaculty.ViewHolder> {

    private List<AlertChatModelFaculty> alertList;

    public AlertChatAdapterFaculty(List<AlertChatModelFaculty> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertChatAdapterFaculty.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_chat_faculty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertChatAdapterFaculty.ViewHolder holder, int position) {
        AlertChatModelFaculty alert = alertList.get(position);
        holder.alertText.setText("New Message From " + alert.getStudentName() + " ( " + alert.getStudentId() + " ) at " + alert.getTimestamp());
        holder.unreadCount.setText(alert.getUnreadCount() + " unread messages");
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView alertText, unreadCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alertText = itemView.findViewById(R.id.alertfcText);
            unreadCount = itemView.findViewById(R.id.unreadCount);
        }
    }
}
