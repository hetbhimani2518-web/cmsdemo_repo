package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertAssignmentStudentAdapter extends RecyclerView.Adapter<AlertAssignmentStudentAdapter.AlertViewHolder> {

    private List<String> alertMessages;

    public AlertAssignmentStudentAdapter(List<String> alertMessages) {
        this.alertMessages = alertMessages;
    }

    @NonNull
    @Override
    public AlertAssignmentStudentAdapter.AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_ass_student, parent, false);
        return new AlertAssignmentStudentAdapter.AlertViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertAssignmentStudentAdapter.AlertViewHolder holder, int position) {
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
