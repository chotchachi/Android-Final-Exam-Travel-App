package com.example.dulich;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BoNhoSharedPreferences {

    public void GanSP(String key, String value, Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String TraSP(String key, Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String data = sharedPreferences.getString(key, "") ;
        return data;
    }

    public void XoaSP(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
