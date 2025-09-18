package com.example.cms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentAdapterFaculty extends RecyclerView.Adapter<AssignmentAdapterFaculty.AssignmentViewHolder> {

    private Context context;
    private List<AssignmentModelFaculty> assignmentList;

    public AssignmentAdapterFaculty(Context context, List<AssignmentModelFaculty> assignmentList) {
        this.assignmentList = assignmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentAdapterFaculty.AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_item_faculty, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentAdapterFaculty.AssignmentViewHolder holder, int position) {
        AssignmentModelFaculty assignment = assignmentList.get(position);

        holder.tvTitle.setText("📘 Subject Name: " + assignment.getTitle());
        holder.tvDeadline.setText("⏰ Deadline: " + assignment.getDeadline());
        holder.tvProgram.setText("🎓 " + assignment.getProgram() + " | " + assignment.getYear() +
                " | " + assignment.getSemester() + " | " + assignment.getDivision());

        holder.btnViewDetails.setOnClickListener(view -> {
            Intent intent = new Intent(context, FacultyAssignmentDetails.class);
            intent.putExtra("title", assignment.getTitle());
            intent.putExtra("description", assignment.getDescription());
            intent.putExtra("deadline", assignment.getDeadline());
            intent.putExtra("drivelink", assignment.getDriveLink());
            intent.putExtra("program", assignment.getProgram());
            intent.putExtra("semester", assignment.getSemester());
            intent.putExtra("year", assignment.getYear());
            intent.putExtra("division", assignment.getDivision());
            intent.putExtra("facultyName", assignment.getFacultyName());
            intent.putExtra("assignmentID", assignment.getAssignmentId());
            intent.putExtra("sentDate", assignment.getCurrentDate());
            intent.putExtra("autoDeleteDate", assignment.getAutoDelete());
            context.startActivity(intent);
        });

//        holder.btnOpenLink.setOnClickListener(v -> {
//            String url = assignment.getDriveLink();
//            if (url != null && !url.isEmpty()) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                v.getContext().startActivity(intent);
//            } else {
//                Toast.makeText(v.getContext(), "No Drive Link Provided", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDeadline, tvProgram;
        Button btnViewDetails;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvProgram = itemView.findViewById(R.id.tvProgramDetails);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
