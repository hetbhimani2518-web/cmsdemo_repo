package com.example.cms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapterFaculty extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_RIGHT = 1;
    private static final int VIEW_TYPE_LEFT = 2;

    private List<ChatModelFaculty> chatList;
    private String facultyId;

    public ChatAdapterFaculty(List<ChatModelFaculty> chatList, String facultyId) {
        this.chatList = chatList;
        this.facultyId = facultyId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_faculty, parent, false);
//        return new ChatViewHolder(view);
        if (viewType == VIEW_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
            return new RightViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
            return new LeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModelFaculty chat = chatList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_RIGHT) {
            ((RightViewHolder) holder).bind(chat);
        } else {
            ((LeftViewHolder) holder).bind(chat);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSenderId().equals(facultyId)) {
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
            messageText = itemView.findViewById(R.id.textMessageSent);
            timestampText = itemView.findViewById(R.id.textTimeSent);
        }

        void bind(ChatModelFaculty chat) {
            messageText.setText(chat.getMessage());
            timestampText.setText(chat.getTimestamp());
        }
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        LeftViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessageReceived);
            timestampText = itemView.findViewById(R.id.textTimeReceived);
        }

        void bind(ChatModelFaculty chat) {
            messageText.setText(chat.getMessage());
            timestampText.setText(chat.getTimestamp());
        }
    }
}

