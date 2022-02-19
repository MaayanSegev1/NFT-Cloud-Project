package com.nft.cloud.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nft.cloud.Adapters.NftProfileAdapter;
import com.nft.cloud.Models.AllNftModel;
import com.nft.cloud.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nft.cloud.Utils.GlobalClass;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;
import com.nft.cloud.Views.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<AllNftModel> allNftModelArrayList;
    private NftProfileAdapter nftAdapter;
    FloatingActionButton fab;
    private CircleImageView profile_image;
    private Uri filePathUri;
    FirebaseStorage storage;
    FirebaseApp app;
    StorageReference storageRef;
    private StorageTask mUploadtask;
    DatabaseReference reff;
    private EditText name, skills;
    CircleImageView img;
    TextView about,uname,likes,nfts;
    private int totalLike=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        reff = FirebaseDatabase.getInstance().getReference().child("NFTS");
        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerview);
        nfts = view.findViewById(R.id.nfts);
        likes = view.findViewById(R.id.likes);
        uname = view.findViewById(R.id.uname);
        about = view.findViewById(R.id.about);
        img = view.findViewById(R.id.profile_image);
        uname.setText(SessionManager.getStringPref(HelperKeys.USER_NAME,getContext()));
        about.setText(SessionManager.getStringPref(HelperKeys.USER_ABOUT,getContext()));
        Glide.with(getActivity()).load(SessionManager.getStringPref(HelperKeys.USER_IMAGE,getContext())).into(img);
        allNftModelArrayList = new ArrayList<>();
        getNftsData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SessionManager.getStringPref(HelperKeys.USER_ID, getActivity()).equals("")) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.register_for_nfc), Toast.LENGTH_SHORT).show();
                } else {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.layout_add_nft);
                    dialog.setTitle("Title");

                    profile_image = dialog.findViewById(R.id.profile_image);
                    name = dialog.findViewById(R.id.name);
                    skills = dialog.findViewById(R.id.skills);
                    ImageView bck = dialog.findViewById(R.id.img_back);
                    MaterialButton save = dialog.findViewById(R.id.save);

                    bck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            onImage();
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (name.getText().toString().length() < 3) {
                                name.setError(getActivity().getString(R.string.field_required));
                            } else if (skills.getText().toString().length() < 3) {
                                skills.setError(getActivity().getString(R.string.field_required));
                            }else if (filePathUri==null) {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.image_required), Toast.LENGTH_SHORT).show();
                            } else {

                                uploadImage();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
        return view;
    }

    public void onImage() {

        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            filePathUri = data.getData();
            Log.i("Tah", filePathUri.toString());
            profile_image.setImageURI(filePathUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage() {
        GlobalClass.showLoading(getActivity(), getString(R.string.please_wait));
        storageRef = storage.getReference("nftimages/");
        if (filePathUri != null) {
            final StorageReference ref = storageRef.child(filePathUri.getLastPathSegment());
            mUploadtask = ref.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    GlobalClass.dismissLoading();
                                    //Task<Uri> downloadUrl = ref.getDownloadUrl();
                                    String content = uri.toString();
                                    String result = content.substring(content.indexOf("%") + 1, content.indexOf("?"));
                                    result = result.substring(2);
                                    if (content.length() > 0) {


                                        sendData(content);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            GlobalClass.dismissLoading();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            mProgressBar.setProgress((int) progress);
                        }
                    });

        }


    }

    public void sendData(String pic) {

        AllNftModel allNftModel = new AllNftModel();
        allNftModel.setName(name.getText().toString());
        allNftModel.setSkills(skills.getText().toString());
        allNftModel.setLikes_users_ids("");
        allNftModel.setLikes_counter(0);
        allNftModel.setImage(pic);
        allNftModel.setUser_id(SessionManager.getStringPref(HelperKeys.USER_ID,getContext()));

        reff.push().setValue(allNftModel);
        Toast.makeText(getContext(), getActivity().getString(R.string.nft_added), Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();


    }

    private void getNftsData() {

        allNftModelArrayList=new ArrayList<>();
        String id=SessionManager.getStringPref(HelperKeys.USER_ID,getContext());
        DatabaseReference reff= FirebaseDatabase.getInstance().getReference("NFTS");
        Query query = reff.orderByChild("user_id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        AllNftModel allNftModel = user.getValue(AllNftModel.class);
                        totalLike = totalLike + allNftModel.getLikes_counter();

                        allNftModel.setKey(user.getKey());
                        allNftModelArrayList.add(allNftModel);

                    }
                }
                nfts.setText(getString(R.string.nfts)+allNftModelArrayList.size());
                likes.setText(getActivity().getString(R.string.likes)+totalLike);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                nftAdapter = new NftProfileAdapter(allNftModelArrayList, getContext());
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