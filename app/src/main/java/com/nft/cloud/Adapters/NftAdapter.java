package com.nft.cloud.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nft.cloud.Models.AllNftModel;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;
import com.nft.cloud.Views.MainActivity;
import com.nft.cloud.Views.MessagesActivity;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class NftAdapter extends RecyclerView.Adapter<NftAdapter.NotificationViewHolder> {
    private List<AllNftModel> allNftModelList;
    Context context;
    public NftAdapter(List<AllNftModel> allNftModelList, Context context)
    {
        this.allNftModelList = allNftModelList;
        this.context = context;
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_nft,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, final int position) {
        holder.tv_ShopName.setText(allNftModelList.get(position).getName());
        Glide.with(context).load(allNftModelList.get(position).getImage()).into(holder.imgnews);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AllNftModel allNftModel=allNftModelList.get(position);
                if (allNftModelList.get(position) != null) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.layout_nft_details);
                    dialog.setTitle("Title");
                    TextView name =  dialog.findViewById(R.id.name);
                    TextView skills =  dialog.findViewById(R.id.skills);
                    CircleImageView profile_image =  dialog.findViewById(R.id.profile_image);
                    ImageView bck =  dialog.findViewById(R.id.img_back);
                    MaterialButton like =  dialog.findViewById(R.id.like);
                    MaterialButton message =  dialog.findViewById(R.id.message);
                    name.setText(allNftModelList.get(position).getName());
                    skills.setText(allNftModelList.get(position).getSkills());
                    Glide.with(context).load(allNftModelList.get(position).getImage()).into(profile_image);
                    String uId= SessionManager.getStringPref(HelperKeys.USER_ID,context);
                    if (allNftModelList.get(position).getLikes_users_ids().contains(uId)){
                        like.setEnabled(false);
                        like.setBackgroundColor(Color.GRAY);

                    }
                    if (uId.equals("")){
                        like.setEnabled(false);
                        like.setBackgroundColor(Color.GRAY);
                        message.setEnabled(false);
                        message.setBackgroundColor(Color.GRAY);
                    }
                    if (uId.equals(allNftModelList.get(position).getUser_id())){
                        message.setEnabled(false);
                        message.setBackgroundColor(Color.GRAY);
                    }
                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(context, MessagesActivity.class);
                            intent.putExtra("userId", allNftModelList.get(position).getUser_id());
                            intent.putExtra("userName", SessionManager.getStringPref(HelperKeys.USER_NAME,context));
                            intent.putExtra("userToken", SessionManager.getStringPref(HelperKeys.User_Access_Token,context));
                            context.startActivity(intent);
                        }
                    });
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String alluIds=allNftModel.getLikes_users_ids();
                            int likeCounter=allNftModel.getLikes_counter();
                            alluIds=alluIds+","+uId;
                            likeCounter=likeCounter+1;
                            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("NFTS");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("likes_users_ids", alluIds);
                            hashMap.put("likes_counter", likeCounter);
                            reff.child(allNftModelList.get(position).getKey()).updateChildren(hashMap);
                            Toast.makeText(context, "Liked Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();

                        }
                    });
                    bck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return allNftModelList.size();

    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgnews;
        TextView tv_ShopName;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ShopName = itemView.findViewById(R.id.tv_ShopName);
            imgnews = itemView.findViewById(R.id.imgnews);

        }

    }
}
