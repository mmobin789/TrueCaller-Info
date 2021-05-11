package com.magicbio.truename;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.magicbio.truename.models.Info;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ahmed Bilal on 12/8/2018.
 */

public class TrueName extends Application {

    private static TrueName instance;
    private static final Gson gson = new Gson();

    public static TrueName getInstance() {
        return instance;
    }

    public static void SaveUserInfo(Info info, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("info", gson.toJson(info));
        editor.apply();
    }

    public static void setIsLogin(Boolean isLogin, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("islogin", isLogin);
        editor.commit();
    }

    public static void setLastCall(String Number, Date time, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        editor.putString(Number, format.format(time));
        editor.commit();
    }

    public static Boolean getIslogin(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("islogin", false);
    }

    public static String getLastCall(String Number, Context context) {
        long differenceDates = 0;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String date = sp.getString(Number, "0");
        Date date1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date1 = format.parse(date);
            System.out.println(date);
            long difference = Math.abs(date1.getTime() - new Date().getTime());
            differenceDates = difference;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Last Call " + differenceDates / 1000 + " Seconds ago";
    }

    public static Info getUserInfo(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return gson.fromJson(sp.getString("info", null), Info.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String process = getProcessName();
            if (!getPackageName().equals(process)) WebView.setDataDirectorySuffix(process);

        }

        MobileAds.initialize(this, getString(R.string.adMob_ID));


        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
    }
}
