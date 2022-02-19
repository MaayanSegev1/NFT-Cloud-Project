package com.nft.cloud.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nft.cloud.Models.Registration;
import com.nft.cloud.R;
import com.nft.cloud.Utils.GlobalClass;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;
import com.nft.cloud.Utils.ValidationChecks;
import com.nft.cloud.Views.MainActivity;

import java.util.HashMap;
import java.util.Map;


public class SigninFragment extends Fragment {

    TextView tv_signUp;
    EditText email,password;
    DatabaseReference reff;
    MaterialButton login;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_signin, container, false);
        reff = FirebaseDatabase.getInstance().getReference().child("Registration");
        login=view.findViewById(R.id.btn_login);
        tv_signUp=view.findViewById(R.id.tv_signUp);
        password=view.findViewById(R.id.login_password);
        email=view.findViewById(R.id.login_email);
        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, new RegisterAccountFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( (ValidationChecks.validateAnyName(password, getString(R.string.enter_password)))
                        && (ValidationChecks.validateEmail(email, getString(R.string.enter_email))
                )
                ) {
                    login_m(email.getText().toString(),password.getText().toString());
                }
            }
        });
        return view;
    }
    private void login_m(final String user_email_, final String user_pass_) {
        GlobalClass.showLoading(getActivity(),getString(R.string.please_wait));

        Query query = reff.orderByChild("email").equalTo(user_email_);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        String userId=user.getRef().getKey();
                        Registration registration = user.getValue(Registration.class);
                        if (registration.getPasswrd().equals(user_pass_)) {
                            GlobalClass.dismissLoading();


                            SessionManager.putStringPref(HelperKeys.USER_ID,userId,getContext());
                            SessionManager.putStringPref(HelperKeys.USER_EMAIL,registration.getEmail(),getContext());
                            SessionManager.putStringPref(HelperKeys.USER_NAME,registration.getFull_name(),getContext());
                            SessionManager.putStringPref(HelperKeys.USER_ABOUT,registration.getUser_about(),getContext());
                            SessionManager.putStringPref(HelperKeys.USER_NICK,registration.getNick_name(),getContext());
                            SessionManager.putStringPref(HelperKeys.USER_IMAGE,registration.getUpic(),getContext());
                            sendRegistrationToServer(userId);


                            //Toast.makeText(login_page.this,userId , Toast.LENGTH_SHORT).show();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Login Error");
                            builder.setPositiveButton("OK", null);
                            builder.setMessage("User Email or Password is Invalid");
                            AlertDialog thealerbox = builder.create();
                            thealerbox.show();
                            GlobalClass.dismissLoading();
                            // Toast.makeText(login_page.this, "wrong email or password!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Login Error");
                    builder.setPositiveButton("OK", null);
                    builder.setMessage("This email is not registered");
                    AlertDialog thealerbox = builder.create();
                    thealerbox.show();
                    GlobalClass.dismissLoading();
                    //Toast.makeText(login_page.this, "users not registered", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///custom code her
            }
        });

    }
    void sendRegistrationToServer(final String ruid) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("main Activity", "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Map<String, Object> map = new HashMap<>();
                        map.put("token", token);
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registration");
                        databaseReference.child(ruid).child("token").setValue(token);
                        Toast.makeText(getContext(), getActivity().getResources().getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                        SessionManager.putStringPref(HelperKeys.User_Access_Token,token,getContext());

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
    }
}