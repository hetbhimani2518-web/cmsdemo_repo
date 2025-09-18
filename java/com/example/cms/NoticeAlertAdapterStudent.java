package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoticeAlertAdapterStudent extends RecyclerView.Adapter<NoticeAlertAdapterStudent.ViewHolder> {
    private List<NoticeAlertModelStudent> noticeList;

    public NoticeAlertAdapterStudent(List<NoticeAlertModelStudent> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeAlertAdapterStudent.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_notice_item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAlertAdapterStudent.ViewHolder holder, int position) {
        NoticeAlertModelStudent notice = noticeList.get(position);
        holder.tvNoticeTitle.setText(notice.getNoticeTitle());
        holder.tvNoticeDate.setText(notice.getNoticeDate());
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoticeTitle, tvNoticeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoticeTitle = itemView.findViewById(R.id.tvNoticeTitle);
            tvNoticeDate = itemView.findViewById(R.id.tvNoticeDate);
        }
    }
}
