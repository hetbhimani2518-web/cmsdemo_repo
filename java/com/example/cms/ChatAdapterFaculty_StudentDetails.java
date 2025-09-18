package com.example.cms;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapterFaculty_StudentDetails extends RecyclerView.Adapter<ChatAdapterFaculty_StudentDetails.StudentViewHolder> {
    //without filter
//    private List<ChatModelFaculty_StudentDetails> studentList;
    private List<ChatModelFaculty_StudentDetails> fullList;
    private List<ChatModelFaculty_StudentDetails> filteredList;
    private Context context;

    //without filter
//    public ChatAdapterFaculty_StudentDetails(List<ChatModelFaculty_StudentDetails> studentList, Context context) {
//        this.studentList = studentList;
//        this.context = context;
//    }

    public ChatAdapterFaculty_StudentDetails(List<ChatModelFaculty_StudentDetails> studentList, Context context) {
        this.fullList = new ArrayList<>(studentList);
        this.filteredList = new ArrayList<>(studentList);
        this.context = context;
    }
//without filter
//    public void updateList(List<ChatModelFaculty_StudentDetails> newList) {
//        studentList = newList;
//        notifyDataSetChanged();
//    }

    public void updateList(List<ChatModelFaculty_StudentDetails> newList) {
        fullList.clear();
        fullList.addAll(newList);
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        filteredList.clear();
        if (TextUtils.isEmpty(text)) {
            filteredList.addAll(fullList);
        } else {
            for (ChatModelFaculty_StudentDetails student : fullList) {
                if (student.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(student);
                }
            }
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ChatAdapterFaculty_StudentDetails.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_student_chat, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapterFaculty_StudentDetails.StudentViewHolder holder, int position) {
        ChatModelFaculty_StudentDetails student = filteredList.get(position);

        holder.name.setText(student.getName());
        holder.details.setText(student.getProgram() + " - " + student.getYear() + " - " + student.getSemester() + " - " + student.getDivision());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, discussion_faculty_chatdisplay.class);
            intent.putExtra("studentId", student.getStudentId());
            intent.putExtra("studentName", student.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();

    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.student_name);
            details = itemView.findViewById(R.id.student_details);
        }
    }
}
