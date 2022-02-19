package com.nft.cloud.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nft.cloud.Adapters.ChatAdapter;
import com.nft.cloud.Models.Registration;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;

import java.util.ArrayList;


public class AllChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Registration> registrationArrayList;
    private ChatAdapter chatAdapter;
    EditText searchText;
    String ownId;
    ProgressBar progressBar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_messages, container, false);
        ownId= SessionManager.getStringPref(HelperKeys.USER_ID,getContext());

        recyclerView = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progress_bar);
        searchText = view.findViewById(R.id.search_text);
        registrationArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(chatAdapter!=null)
                {

                    chatAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getuserchats();
        return view;
    }
    private void getuserchats() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatLists");
        Query query = reference
                .child(ownId);
        registrationArrayList = new ArrayList<>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    if (searchText.getText().toString().equals("")) {
                        registrationArrayList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String sender = snapshot.child("id").getValue(String.class);
                            getUser(sender);
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getActivity().getString(R.string.no_chats), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser(String sender_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registration").child(sender_id);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchText.getText().toString().equals("")) {

                    Registration user = dataSnapshot.getValue(Registration.class);
                    user.setKey(dataSnapshot.getKey());
                    assert user != null;
                    if (!user.getKey().equals(ownId)) {
                        registrationArrayList.add(user);
                    }
                    progressBar.setVisibility(View.GONE);
                    if (registrationArrayList.isEmpty()) {
                        Toast.makeText(getContext(), getActivity().getString(R.string.no_chats), Toast.LENGTH_SHORT).show();
                    } else {

                    }
                    chatAdapter = new ChatAdapter(getActivity(), registrationArrayList, false);
                    recyclerView.setAdapter(chatAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}