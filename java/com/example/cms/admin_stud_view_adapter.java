package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class admin_stud_view_adapter extends RecyclerView.Adapter<admin_stud_view_adapter.ViewHolder>{

    private List<admin_stud_view_model> studentList;

    public admin_stud_view_adapter(List<admin_stud_view_model> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public admin_stud_view_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_stud_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull admin_stud_view_adapter.ViewHolder holder, int position) {
        admin_stud_view_model student = studentList.get(position);
        holder.tvName.setText(student.getName());
        holder.tv_st_ct.setText("Stream: " + student.getStudentId() + "\nContact: " + student.getContact());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tv_st_ct;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_student_name);
            tv_st_ct = itemView.findViewById(R.id.tv_stud_stream_contact);
        }
    }
}
