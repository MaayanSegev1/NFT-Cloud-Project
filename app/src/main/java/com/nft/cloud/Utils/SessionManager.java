package com.nft.cloud.Utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {


    private static final String PREF_NAME = "nftcloud";

    public static void  clearsession(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

    public static void putStringPref(String key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static String getStringPref(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }
}