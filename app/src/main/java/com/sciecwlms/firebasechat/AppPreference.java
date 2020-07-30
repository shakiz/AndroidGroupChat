package com.sciecwlms.firebasechat;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    private SharedPreferences mPreferences;
    private String PREF_NAME = "MyGroupChat";
    private String KEY_EMAIL = "email";
    private String KEY_ID = "id";
    private SharedPreferences.Editor editor;

    public AppPreference(Context mContext){
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public void setEmail(String email){
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail(){
        return  mPreferences.getString(KEY_EMAIL, null);
    }

    public void setId(String id){
        editor.putString(KEY_ID, id);
        editor.commit();
    }

    public String getId(){
        return  mPreferences.getString(KEY_ID, null);
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }
}
