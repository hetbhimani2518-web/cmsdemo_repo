package com.example.cms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertChatAdapterStudent extends RecyclerView.Adapter<AlertChatAdapterStudent.AlertViewHolder> {

    private List<AlertChatModelStudent> alertList;
    private Context context;

    public AlertChatAdapterStudent(List<AlertChatModelStudent> alertList, Context context) {
        this.alertList  = alertList;
        this.context = context;

    }

    @NonNull
    @Override
    public AlertChatAdapterStudent.AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_chat_stud, parent, false);
        return new AlertChatAdapterStudent.AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertChatAdapterStudent.AlertViewHolder holder, int position) {
        AlertChatModelStudent alert = alertList.get(position);
        holder.alertText.setText("New message from " + alert.getFacultyName() + " at " + alert.getTimestamp());
        holder.countText.setText(alert.getUnreadCount() + " unread messages" );
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView alertText, countText;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertText = itemView.findViewById(R.id.alertText);
            countText = itemView.findViewById(R.id.countText);
        }
    }
}
