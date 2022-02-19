package com.nft.cloud.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.nft.cloud.Adapters.MessageAdapter;
import com.nft.cloud.Models.Chat;
import com.nft.cloud.Models.MessageNotificationModel;
import com.nft.cloud.Models.Registration;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessagesActivity extends AppCompatActivity {
    RelativeLayout user_profile_view;
    CircleImageView profile_pic;
    TextView tv_username;

    Button btn_send;
    EditText editText;


    DatabaseReference reference;
    Intent intent;
    String userId,userName,userToken;

    MessageAdapter messageAdapter;
    List<Chat> chat_list;

    ValueEventListener seenListener;
    String ownId;
    RecyclerView recyclerView;
    private String devicetoken = "AAAA2nDH1Ac:APA91bHtjTMR_6yPDG-w_KYgVKmtTzrMS8gh_kGT33IzhW8O8fM7W0g9UpWRhVD0ZgEbVrlOd_FiwgYMFdUR9sd7lJuOiJOx1VNfc8AIA5AHNtDeyYA2_u5N2Tex-Dz6a42bsq82mNoQ";

    @Override
    protected void attachBaseContext(Context base) {
        base = LocaleChanger.configureBaseContext(base);
        super.attachBaseContext(base);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);

    }

    @Override
    protected void onDestroy() {
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        ownId = SessionManager.getStringPref(HelperKeys.USER_ID,getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MessagesActivity.this, UserChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        user_profile_view = findViewById(R.id.user_profile_view);
        profile_pic = findViewById(R.id.profile_pic);
        tv_username = findViewById(R.id.username);
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName= intent.getStringExtra("userName");
        userToken= intent.getStringExtra("userToken");
        btn_send = findViewById(R.id.sendBtn);
        editText = findViewById(R.id.msg_input);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(MessagesActivity.this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if(!message.trim().equals("")){
                    sendMessage(ownId, userId, message);
                    sendnotificationtouser(SessionManager.getStringPref(HelperKeys.USER_NAME,getApplicationContext()),userToken,message,ownId);
                }
                editText.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Registration").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Registration user = dataSnapshot.getValue(Registration.class);
                assert user != null;
                tv_username.setText(user.getNick_name());
                userName=user.getNick_name();
                userToken=user.getToken();
                if(user.getUpic().equals("")){
                    profile_pic.setImageResource(R.drawable.profile_icon_white);
                } else{
                    Glide.with(getApplicationContext()).load(user.getUpic()).into(profile_pic);
                }
                readMessage(ownId, userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Database Error", "onCancelled: Error -> "+ databaseError.toString());
            }
        });

        isSeen(userId);
    }

    private void isSeen(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(ownId) && chat.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);
        hashMap.put("time", ServerValue.TIMESTAMP);

        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference chatReferenceForSender = FirebaseDatabase.getInstance().getReference("ChatLists")
                .child(ownId)
                .child(userId);

        chatReferenceForSender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatReferenceForSender.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatReferenceForReceiver = FirebaseDatabase.getInstance().getReference("ChatLists")
                .child(receiver)
                .child(ownId);

        chatReferenceForReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatReferenceForReceiver.child("id").setValue(ownId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(final String readerId, final String senderId){
        chat_list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(readerId) && chat.getSender().equals(senderId) || chat.getReceiver().equals(senderId) && chat.getSender().equals(readerId)) {
                        chat_list.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessagesActivity.this, chat_list);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("DB Error in readMessage", "onCancelled: Error -> "+ databaseError.toString());
            }
        });


    }



    private void Status(final String status){
        reference = FirebaseDatabase.getInstance().getReference("Registration").child(ownId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", status);
                    reference.updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
    }

    private  void sendnotificationtouser( String userName, String userToken, String message,String usrId)
    {
        sendGCM(userName,message,userToken,usrId);

    }
    public void sendGCM(String title, String msg, String token,String usrId) {
        Gson gson = new Gson();
        MessageNotificationModel notificationModel = new MessageNotificationModel();
        notificationModel.notification.title = title;
        notificationModel.notification.body = msg;
        notificationModel.notification.type = usrId;
        notificationModel.data.title = title;
        notificationModel.data.body = msg;
        notificationModel.data.type = usrId;
        notificationModel.to = token;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),
                gson.toJson(notificationModel));
        final Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + devicetoken)
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp fail", e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("okhttp response", response.toString());
            }
        });
    }

}
