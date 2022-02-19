package com.nft.cloud.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.franmontiel.localechanger.LocaleChanger;
import com.google.android.material.button.MaterialButton;
import com.nft.cloud.R;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;
import com.nft.cloud.Views.MainActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class SettingsFragment extends Fragment {


    public static final List<Locale> SUPPORTED_LOCALES =
            Arrays.asList(
                    new Locale("en", "US"),
                    new Locale("iw", "rIL")

            );

    MaterialButton savechanges;
    CheckBox darkmode;
    private RadioGroup radioGroup;
    private boolean darkcheck=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings, container, false);
        savechanges=view.findViewById(R.id.savechanges);
        darkmode=view.findViewById(R.id.darkmode);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        if (SessionManager.getStringPref(HelperKeys.MODE,getContext()).equals("dark")){
            darkmode.setChecked(true);
        }

        if (SessionManager.getStringPref(HelperKeys.Language,getContext()).equals("hebrew")){
            RadioButton b = (RadioButton) view.findViewById(R.id.hebrew);
            b.setChecked(true);
        }
        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                darkcheck= b;

            }
        });
        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == -1) {

                }else{
                    if (checkedRadioButtonId == R.id.eng) {
                        LocaleChanger.setLocale(SUPPORTED_LOCALES.get(0));
                        SessionManager.putStringPref(HelperKeys.Language,"english",getActivity());
                    } else if (checkedRadioButtonId == R.id.hebrew) {
                        LocaleChanger.setLocale(SUPPORTED_LOCALES.get(1));
                        SessionManager.putStringPref(HelperKeys.Language,"hebrew",getActivity());

                    }
                    if (darkcheck){

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SessionManager.putStringPref(HelperKeys.MODE,"dark",getActivity());
                    }else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SessionManager.putStringPref(HelperKeys.MODE,"light",getActivity());
                    }
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        return view;
    }
}