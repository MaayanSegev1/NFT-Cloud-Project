package com.nft.cloud.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.nft.cloud.Models.Chat;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int  MSG_TYPE_SENT = 1;
    private static final int  MSG_TYPE_RECEIVED = 0;
    private Context context;
    private List<Chat> chatList;
    String ownId;


    public MessageAdapter(Context context, List<Chat> chatList){
        this.context = context;
        this.chatList = chatList;


        ownId = SessionManager.getStringPref(HelperKeys.USER_ID,context);
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_chat_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.received_chat_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Chat chat = chatList.get(i);
        viewHolder.tv_message_text.setText(chat.getMessage());
        Log.i("DEBUG via COUNT", "onBindViewHolder: isSeen = " + chat.getIsSeen() );

        if (i <= chatList.size()-1){
            if (chat.getIsSeen()){
                viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_icon));
            } else {
                viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delivered_icon));
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_message_text;
        ImageView iv_seen;
        TextView messageTimeStamp;

        ViewHolder(View itemView){
            super(itemView);
            tv_message_text = itemView.findViewById(R.id.messageText);
            iv_seen = itemView.findViewById(R.id.iv_seen);
            messageTimeStamp = itemView.findViewById(R.id.messageTimeStamp);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSender().equals(ownId)){
            return MSG_TYPE_SENT;
        } else {
            return MSG_TYPE_RECEIVED;
        }
    }
}

