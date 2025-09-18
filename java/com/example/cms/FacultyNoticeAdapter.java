package com.example.cms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FacultyNoticeAdapter extends RecyclerView.Adapter<FacultyNoticeAdapter.NoticeViewHolder> {

    private Context context;
    private ArrayList<FacultyNoticeModel> noticeList;

    public FacultyNoticeAdapter(Context context, ArrayList<FacultyNoticeModel> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public FacultyNoticeAdapter.NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_notice_item , parent , false);
        return new FacultyNoticeAdapter.NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyNoticeAdapter.NoticeViewHolder holder, int position) {
        FacultyNoticeModel notice = noticeList.get(position);
        holder.tvTitle.setText(notice.getTitle());
        holder.tvDate.setText("Date: " + notice.getDate());
        holder.tvProgramInfo.setText(notice.getProgram() + " - " + notice.getYear() + " - " + notice.getSemester() + " - " + notice.getDivision());

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, FacultyNoticeDetails.class);
            intent.putExtra("title", notice.getTitle());
            intent.putExtra("content", notice.getContent());
            intent.putExtra("date", notice.getDate());
            intent.putExtra("program", notice.getProgram());
            intent.putExtra("semester", notice.getSemester());
            intent.putExtra("year", notice.getYear());
            intent.putExtra("division" , notice.getDivision());
            intent.putExtra("facultyName", notice.getFacultyName());
            intent.putExtra("noticeId", notice.getNoticeId());
            intent.putExtra("autoDeleteDate" , notice.getAutoDeleteDate());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvProgramInfo;
        Button btnViewDetails;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNoticeTitle);
            tvDate = itemView.findViewById(R.id.tvNoticeDate);
            tvProgramInfo = itemView.findViewById(R.id.tvProgramInfo);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
