package com.nft.cloud.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.nft.cloud.Models.Registration;
import com.nft.cloud.R;
import com.nft.cloud.Utils.GlobalClass;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;
import com.nft.cloud.Views.MainActivity;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileConstructionFragment extends Fragment {

    CircleImageView imageView;
    EditText nick, full_name , about ;
    MaterialButton btn_signUp;
    private Uri filePathUri;
    DatabaseReference reff;
    Registration reg;
    private String email,pass;
    FirebaseStorage storage;
    FirebaseApp app;
    StorageReference storageRef;
    private StorageTask mUploadtask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile_construction, container, false);
        reff= FirebaseDatabase.getInstance().getReference().child("Registration");
        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);
        reg=new Registration();
        nick=view.findViewById(R.id.signup_nick);
        full_name=view.findViewById(R.id.signup_full_name);
        about=view.findViewById(R.id.signup_about);
        btn_signUp=view.findViewById(R.id.btn_signUp);
        imageView=view.findViewById(R.id.profile_image);
        Bundle bundle = this.getArguments();

        if(bundle != null){

            email=bundle.getString("email");
            pass=bundle.getString("password");
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImage();
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(nick.getText().toString())) {
                    nick.setError(getString(R.string.field_required));
                }  else if (full_name.getText().toString().length() < 6) {
                    full_name.setError(getString(R.string.field_required));
                }  else if (about.getText().toString().length() < 6) {
                    about.setError(getString(R.string.field_required));
                } else {
                    uploadImage();
                }


            }
        });
        return view;

    }
    public void sendSignupdata(String pic){
        reg.setEmail(email);
        reg.setPasswrd(pass);
        reg.setNick_name(nick.getText().toString());
        reg.setFull_name(full_name.getText().toString());
        reg.setUser_about(about.getText().toString());
        reg.setUpic(pic);
        Query query = reff.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), getActivity().getString(R.string.email_already_registered),Toast.LENGTH_LONG).show();


                }else{

                    reff.push().setValue(reg);
                    Toast.makeText(getContext(), getActivity().getString(R.string.signup_success_login),Toast.LENGTH_LONG).show();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    fm.popBackStack();


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///custom code her
            }
        });
    }
    public void onImage() {

        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    private void uploadImage() {
        GlobalClass.showLoading(getActivity(),getString(R.string.please_wait));
        storageRef = storage.getReference("images/");
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


                                        sendSignupdata(content);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            filePathUri = data.getData();
            Log.i("Tah", filePathUri.toString());
            imageView.setImageURI(filePathUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }
}