package com.example.cms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private List<NoticeModel> noticeList;

    public NoticeAdapter(List<NoticeModel> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeModel notice = noticeList.get(position);

        holder.tvTitle.setText("Title: " + notice.getTitle());
        holder.tvContent.setText("Content: " + notice.getContent());
        holder.tvDate.setText("Recevied On: " + notice.getDate());
        holder.tvFacultyName.setText("Recevied From: " + notice.getFacultyName());

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate, tvFacultyName;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notice_title);
            tvContent = itemView.findViewById(R.id.tv_notice_content);
            tvDate = itemView.findViewById(R.id.tv_notice_date);
            tvFacultyName = itemView.findViewById(R.id.tv_faculty_name);
        }
    }
}
