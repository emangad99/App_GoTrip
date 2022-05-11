package com.mayv.gotrip;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MyShared {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String EMAIL_KEY = "UserEmail";
    private final String LANGUAGE_KEY = "AppLanguage";
    private final String UID_KEY = "UserID";
    private final String ULB_KEY = "UserLoggedBefore";
    private final String NFL_KEY = "NeedFirebaseLoad";
    private final String PTL_KEY = "PastTripsLoad";
    private final String CTI_KEY = "ClickedTripId";
    private final String CFB_KEY = "CameFromBroadcast";

    public MyShared(Context context, Activity activity) {
        String PREF_KEY = "UserPref";
        if(sharedPreferences == null || editor == null){
            sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public void setEmailPref(String email) {
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    public void setUIDPref(String uid) {
        editor.putString(UID_KEY, uid);
        editor.apply();
    }

    public void setULBPref(boolean ulb) {
        editor.putBoolean(ULB_KEY, ulb);
        editor.apply();
    }

    public void setNFLPref(boolean nfl) {
        editor.putBoolean(NFL_KEY, nfl);
        editor.apply();
    }

    public void setPTLPref(boolean ptl) {
        editor.putBoolean(PTL_KEY, ptl);
        editor.apply();
    }

    public void setLanguagePref(String lang) {
        editor.putString(LANGUAGE_KEY, lang);
        editor.apply();
    }

    public void setCTIPref(int cti) {
        editor.putInt(CTI_KEY, cti);
        editor.apply();
    }

    public void setCFBPref(boolean cfb) {
        editor.putBoolean(CFB_KEY, cfb);
        editor.apply();
    }

    public String getEmailPref() {
        return sharedPreferences.getString(EMAIL_KEY, "example@emp.exo");
    }

    public String getUIDPref() {
        //User Id
        return sharedPreferences.getString(UID_KEY, "example");
    }

    public boolean getULBPref() {
        //User Logged Before
        return sharedPreferences.getBoolean(ULB_KEY, false);
    }

    public boolean getNFLPref() {
        //Need Firebase Load
        return sharedPreferences.getBoolean(NFL_KEY, true);
    }

    public boolean getPTLPref() {
        //Past Trips Firebase Load
        return sharedPreferences.getBoolean(PTL_KEY, true);
    }

    public String getLanguagePref() {
        //Application Language
        return sharedPreferences.getString(LANGUAGE_KEY, "en");
    }

    public int getCTIPref() {
        //Clicked Trip Id
        return sharedPreferences.getInt(CTI_KEY, 0);
    }

    public boolean getCFBPref() {
        //Came From Broadcast
        return sharedPreferences.getBoolean(CFB_KEY, false);
    }
}
