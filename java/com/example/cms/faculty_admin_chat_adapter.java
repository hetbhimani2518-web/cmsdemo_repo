package com.example.cms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class faculty_admin_chat_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_RIGHT = 1;
    private static final int VIEW_TYPE_LEFT = 2;

    private List<faculty_admin_chat_model> chatList;
    private String currentFacultyId;

    public faculty_admin_chat_adapter(List<faculty_admin_chat_model> chatList, String currentFacultyId) {
        this.chatList = chatList;
        this.currentFacultyId = currentFacultyId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new RightViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new LeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        faculty_admin_chat_model chat = chatList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_RIGHT) {
            ((RightViewHolder) holder).bind(chat);
        } else {
            ((LeftViewHolder) holder).bind(chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSenderId().equals(currentFacultyId)) {
            return VIEW_TYPE_RIGHT;
        } else {
            return VIEW_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class RightViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        RightViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timestampText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(faculty_admin_chat_model chat) {
            messageText.setText(chat.getMessage());
            timestampText.setText(chat.getTimestamp());
        }
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        LeftViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timestampText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(faculty_admin_chat_model chat) {
            messageText.setText(chat.getMessage());
            timestampText.setText(chat.getTimestamp());
        }
    }
}
