package com.nft.cloud.Utils;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.franmontiel.localechanger.LocaleChanger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;


public class GlobalClass extends Application implements Serializable {
    public static final List<Locale> SUPPORTED_LOCALES =
            Arrays.asList(
                    new Locale("en", "US"),
                     new Locale("iw", "rIL")

            );
    public static SpotsDialog dialog;
    private static boolean isLoading = false;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleChanger.onConfigurationChanged();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleChanger.initialize(getApplicationContext(), SUPPORTED_LOCALES);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    public static void showLoading(Activity activity, String message) {
        if (!isLoading) {
            isLoading = true;
            dialog = new SpotsDialog(activity, message);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
    }

    public static void dismissLoading() {
        try {
            if (isLoading) {
                isLoading = false;
                dialog.dismiss();
            }
        } catch (Exception e) {
            isLoading = true;
        }
    }
}
