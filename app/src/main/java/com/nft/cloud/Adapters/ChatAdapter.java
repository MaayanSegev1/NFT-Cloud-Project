package com.nft.cloud.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nft.cloud.Models.Chat;
import com.nft.cloud.Models.Registration;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;
import com.nft.cloud.Views.MessagesActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Registration> exampleListFull;
    private List<Registration> exampleList;
    private boolean isInChat;
    private String lastMessage;
    String ownId;

    public ChatAdapter(Context context, List<Registration> exampleList, Boolean isInChat) {
        this.context = context;
        this.isInChat = isInChat;
        this.exampleList = exampleList;
        this.exampleListFull=exampleList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ownId = SessionManager.getStringPref(HelperKeys.USER_ID,context);
        final Registration user = exampleListFull.get(i);
        viewHolder.tv_username.setText(user.getNick_name());
        if (user.getUpic().equals("")) {
            viewHolder.profile_pic.setImageResource(R.drawable.profile_icon);
        } else {
            Glide.with(context).load(user.getUpic()).into(viewHolder.profile_pic);
        }

        Log.i("USER LIST : ", "onBindViewHolder: USER DETAILS = " + user.toString());

        if (isInChat) {
            checkLastMessage(user.getKey(), viewHolder.tv_user_about_or_last_message);
        } else {
            viewHolder.tv_user_about_or_last_message.setText(user.getUser_about());
        }
        if (isInChat) {
            Log.i("USER STATUS", "onBindViewHolder: status = " + user.getChatstatus());

            switch (user.getChatstatus()) {
                case "online":
                    viewHolder.status.setImageResource(R.drawable.shape_bubble_online);
                    break;
                case "offline":
                    viewHolder.status.setImageResource(R.drawable.shape_bubble_offline);
                    break;

            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("userId", user.getKey());
                intent.putExtra("userName", user.getNick_name());
                intent.putExtra("userToken", user.getToken());
                context.startActivity(intent);
            }
        });
        viewHolder.tv_user_about_or_last_message.setText(user.getUser_about());

    }

    @Override
    public int getItemCount() {
        return exampleListFull.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;
        TextView tv_user_about_or_last_message;
        ImageView profile_pic;
        CircleImageView status;

        ViewHolder(View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.username);
            tv_user_about_or_last_message = itemView.findViewById(R.id.tv_user_about_or_last_message);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            status = itemView.findViewById(R.id.status_icon);
        }
    }

    private void checkLastMessage(final String userId, final TextView tv_user_about_or_last_message) {
        lastMessage = "";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        if (chat.getReceiver().equals(ownId) && chat.getSender().equals(userId) ||
                                chat.getReceiver().equals(userId) && chat.getSender().equals(ownId)) {
                            lastMessage = chat.getMessage();
                        }
                    }


                    switch (lastMessage) {
                        case "":
                            tv_user_about_or_last_message.setText("");
                            break;

                        default:
                            tv_user_about_or_last_message.setText(lastMessage);
                            break;
                    }
                    lastMessage = "";
                } catch (NullPointerException e) {
                    Log.i("NullPointerException", "onDataChange: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            exampleListFull=new ArrayList<>();
            if (charString.isEmpty()) {

                exampleListFull.addAll(exampleList);
            } else {
                List<Registration> filteredList = new ArrayList<>();
                for (Registration row : exampleList) {


                    // name match condition. this might differ depending on your requirement
                    if (row.getFull_name().toLowerCase().contains(charString.toLowerCase()) || row.getNick_name().toLowerCase().contains(charString.toLowerCase())) {
                        filteredList.add(row);
                    }
                }

                exampleListFull = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = exampleListFull;
            return filterResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleListFull = (ArrayList<Registration>) results.values;
            notifyDataSetChanged();
        }
    };
}
