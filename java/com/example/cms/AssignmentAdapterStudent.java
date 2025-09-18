package com.example.cms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssignmentAdapterStudent extends RecyclerView.Adapter<AssignmentAdapterStudent.ViewHolder> {


    private List<AssignmentModelStudent> assignmentList;

    public AssignmentAdapterStudent(List<AssignmentModelStudent> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentModelStudent assignment = assignmentList.get(position);

        holder.tvTitle.setText("📘 Subject: " + assignment.getTitle());
        holder.tvDescription.setText(assignment.getDescription());
        holder.tvDeadline.setText("⏰ Deadline: " + assignment.getDeadline());
        holder.tvFacultyName.setText("By: " + assignment.getFacultyName());
        holder.tvDriveLink.setText("View Assignment");
        holder.tvSentDate.setText("Sent On: " + assignment.getCurrentDate());

        // Open link when clicked
        holder.tvDriveLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(assignment.getDriveLink()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDeadline, tvFacultyName, tvDriveLink , tvSentDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvDescription = itemView.findViewById(R.id.tvAssignmentDesc);
            tvDeadline = itemView.findViewById(R.id.tvAssignmentDeadline);
            tvFacultyName = itemView.findViewById(R.id.tvFacultyName);
            tvDriveLink = itemView.findViewById(R.id.tvDriveLink);
            tvSentDate = itemView.findViewById(R.id.tvAssignmentSentDate);

        }
    }
}
