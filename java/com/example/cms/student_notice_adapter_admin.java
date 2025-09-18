package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class student_notice_adapter_admin extends RecyclerView.Adapter<student_notice_adapter_admin.NoticeViewHolder> {

    private ArrayList<student_notice_model_admin> noticeList;

    public student_notice_adapter_admin(ArrayList<student_notice_model_admin> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public student_notice_adapter_admin.NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_notice_admin_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull student_notice_adapter_admin.NoticeViewHolder holder, int position) {
        student_notice_model_admin notice = noticeList.get(position);
        holder.title.setText(notice.getTitle());
        holder.content.setText(notice.getContent());
        holder.date.setText("Recived On: " + notice.getDate());
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noticeTitle);
            content = itemView.findViewById(R.id.noticeContent);
            date = itemView.findViewById(R.id.noticeDate);
        }
    }

}
