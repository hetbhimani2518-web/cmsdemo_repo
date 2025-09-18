package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertNoticeStudentAdapter extends RecyclerView.Adapter<AlertNoticeStudentAdapter.AlertViewHolder> {

    private List<String> alertMessages;

    public AlertNoticeStudentAdapter(List<String> alertMessages) {
        this.alertMessages = alertMessages;
    }

    @NonNull
    @Override
    public AlertNoticeStudentAdapter.AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_student, parent, false);
        return new AlertViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertNoticeStudentAdapter.AlertViewHolder holder, int position) {
        holder.tvAlertMessage.setText(alertMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return alertMessages.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlertMessage;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlertMessage = itemView.findViewById(R.id.tvAlertMessage);
        }
    }
}
