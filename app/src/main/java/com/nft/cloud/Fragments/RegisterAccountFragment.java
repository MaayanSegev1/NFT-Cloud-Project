package com.nft.cloud.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nft.cloud.Models.Registration;
import com.nft.cloud.R;
import com.google.android.material.button.MaterialButton;


public class RegisterAccountFragment extends Fragment {

    TextView tv_signIn;
    MaterialButton btn_signUp;
    EditText email,pass,cpass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_register_account, container, false);
        email=view.findViewById(R.id.signup_email);
        pass=view.findViewById(R.id.signup_password);
        cpass=view.findViewById(R.id.signup_cPassword);
        tv_signIn=view.findViewById(R.id.tv_signIn);
        btn_signUp=view.findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError(getString(R.string.field_required));
                } else if (!pass.getText().toString().trim().equals(cpass.getText().toString().trim())) {
                    pass.setError(getString(R.string.field_required));
                    cpass.setError(getString(R.string.field_required));
                } else if (pass.getText().toString().length() < 6) {
                    pass.setError(getString(R.string.password_length));
                } else if (!isValidateEmail(email.getText().toString().trim())) {
                    email.setError(getString(R.string.enter_email));

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email.getText().toString());
                    bundle.putString("password", pass.getText().toString());
                    ProfileConstructionFragment fragobj = new ProfileConstructionFragment();
                    fragobj.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, fragobj);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }


            }
        });
        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }
    private boolean isValidateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}