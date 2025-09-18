package com.example.cms;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapterStudent extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final int VIEW_TYPE_RIGHT = 1;
    private static final int VIEW_TYPE_LEFT = 2;

    private List<ChatModelStudent> chatList;
    private String currentStudentId;

    public ChatAdapterStudent(List<ChatModelStudent> chatList, String currentStudentId) {
        this.chatList = chatList;
        this.currentStudentId = currentStudentId;
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
        ChatModelStudent chat = chatList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_RIGHT) {
            ((RightViewHolder) holder).bind(chat);
        } else {
            ((LeftViewHolder) holder).bind(chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSenderId().equals(currentStudentId)) {
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

        void bind(ChatModelStudent chat) {
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

        void bind(ChatModelStudent chat) {
            messageText.setText(chat.getMessage());
            timestampText.setText(chat.getTimestamp());
        }
    }
}



//--------------------If nothing work use below code----------------------
//private List<ChatModelStudent> chatList;
//private String currentUserId;
//
//public ChatAdapterStudent( List<ChatModelStudent> chatList, String currentUserId) {
//    this.chatList = chatList;
//    this.currentUserId = currentUserId;
//}
//
//
//@NonNull
//@Override
//public ChatAdapterStudent.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//    View view;
//    if (viewType == 1) {
//        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sender, parent, false);
//    } else {
//        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receiver, parent, false);
//    }
//    return new ChatViewHolder(view);
//}
//
//@Override
//public void onBindViewHolder(@NonNull ChatAdapterStudent.ChatViewHolder holder, int position) {
//    ChatModelStudent chatMessage = chatList.get(position);
//    holder.tvMessage.setText(chatMessage.getMessage());
//    String formattedDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
//            .format(new Date(chatMessage.getTimestamp()));
//
//    holder.tvTimestamp.setText(formattedDate); // Set formatted timestamp
//
//    if (!chatMessage.isRead() && !chatMessage.getSenderID().equals(currentUserId)) {
//        holder.tvStatus.setVisibility(View.VISIBLE);
//        holder.tvStatus.setText("Unread");
//    } else {
//        holder.tvStatus.setVisibility(View.GONE);
//    }
//}
//
//@Override
//public int getItemCount() {
//    return chatList.size();
//}
//
//public static class ChatViewHolder extends RecyclerView.ViewHolder {
//    TextView tvMessage, tvTimestamp, tvStatus;
//
//    public ChatViewHolder(@NonNull View itemView) {
//        super(itemView);
//        tvMessage = itemView.findViewById(R.id.tvMessage);
//        tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
//        tvStatus = itemView.findViewById(R.id.tvStatus);
//    }
//}