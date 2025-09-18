package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class faculty_notice_adapter_admin extends RecyclerView.Adapter<faculty_notice_adapter_admin.ViewHolder> {

    private ArrayList<faculty_notice_model_admin> noticeList;

    public faculty_notice_adapter_admin(ArrayList<faculty_notice_model_admin> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public faculty_notice_adapter_admin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faculty_notice_admin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull faculty_notice_adapter_admin.ViewHolder holder, int position) {
        faculty_notice_model_admin notice = noticeList.get(position);
        holder.txtTitle.setText(notice.getTitle());
        holder.txtContent.setText(notice.getContent());
        holder.txtDate.setText("Date: " + notice.getDate());
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtContent, txtDate;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
