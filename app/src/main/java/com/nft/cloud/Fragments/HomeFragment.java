package com.nft.cloud.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nft.cloud.Adapters.NftAdapter;
import com.nft.cloud.Adapters.NftProfileAdapter;
import com.nft.cloud.Models.AllNftModel;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<AllNftModel> allNftModelArrayList,originalallNftModelArrayList;
    private NftAdapter nftAdapter;
    Spinner spinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        spinner = view.findViewById(R.id.spinner);
        allNftModelArrayList = new ArrayList<>();
        originalallNftModelArrayList = new ArrayList<>();
        getNftsData();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (allNftModelArrayList.size()!=0){
                    if (i==0){
                        allNftModelArrayList.clear();
                        allNftModelArrayList.addAll(originalallNftModelArrayList);
                        nftAdapter.notifyDataSetChanged();
                    }else if (i==1){
                        allNftModelArrayList.clear();
                        allNftModelArrayList.addAll(originalallNftModelArrayList);
                        nftAdapter.notifyDataSetChanged();

                    }else if (i==2){


                        Collections.shuffle(allNftModelArrayList);
                        nftAdapter.notifyDataSetChanged();

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }
    private void getNftsData() {

        allNftModelArrayList=new ArrayList<>();
        originalallNftModelArrayList=new ArrayList<>();

        DatabaseReference reff= FirebaseDatabase.getInstance().getReference("NFTS");
        Query query = reff.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        AllNftModel allNftModel = user.getValue(AllNftModel.class);

                        allNftModel.setKey(user.getKey());
                        allNftModelArrayList.add(allNftModel);
                        originalallNftModelArrayList.add(allNftModel);

                    }
                }

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                nftAdapter = new NftAdapter(allNftModelArrayList, getContext());
                recyclerView.setAdapter(nftAdapter);
                nftAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///custom code her
            }
        });

    }
}