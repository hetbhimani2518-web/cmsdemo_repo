package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class admin_fac_view_adapter extends RecyclerView.Adapter<admin_fac_view_adapter.ViewHolder> {

    private List<admin_fac_view_model> facultyList;

    public admin_fac_view_adapter(List<admin_fac_view_model> facultyList) {
        this.facultyList = facultyList;
    }

    @NonNull
    @Override
    public admin_fac_view_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_fac_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull admin_fac_view_adapter.ViewHolder holder, int position) {
        admin_fac_view_model faculty = facultyList.get(position);
        holder.tvName.setText(faculty.getName());
        holder.tv_faculty_des_contact.setText("Designation: " + faculty.getDesignation() + "\nContact: " + faculty.getContact());
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tv_faculty_des_contact;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_faculty_name);
            tv_faculty_des_contact = itemView.findViewById(R.id.tv_faculty_des_contact);
        }
    }

}
